package services;

import dao.BookingDAO;
import dao.CarDAO;
import models.Booking;
import models.Car;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * BookingService encapsulates booking lifecycle operations and ensures car
 * availability/status is kept in sync with bookings.
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final CarDAO carDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.carDAO = new CarDAO();
    }

    public BookingService(BookingDAO bookingDAO, CarDAO carDAO) {
        this.bookingDAO = bookingDAO;
        this.carDAO = carDAO;
    }

    public boolean createBooking(int userId, int carId, LocalDate start, LocalDate end) {
        if (start == null || end == null || !end.isAfter(start)) {
            return false;
        }

        // Check availability
        if (!bookingDAO.isCarAvailable(carId, start, end)) {
            return false;
        }

        // Fetch car for pricing
        Car car = carDAO.getById(carId);
        if (car == null || car.getPricePerDay() == null) {
            return false;
        }

        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) {
            days = 1; // minimum 1 day

        }
        BigDecimal total = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Booking booking = new Booking(
                0,
                userId,
                carId,
                start,
                end,
                "pending",
                total,
                LocalDateTime.now()
        );

        // Use transactional create to avoid double-booking
        boolean ok = bookingDAO.createBookingTransactional(booking);
        if (ok) {
            // Optionally set car status to reserved to avoid races before admin approval
            carDAO.updateCarStatus(carId, "reserved");
        }
        return ok;
    }

    public boolean approveBooking(int bookingId) {
        // Mark booking confirmed
        boolean ok = bookingDAO.updateBookingStatus(bookingId, "confirmed");
        if (ok) {
            // Set car reserved (if not already)
            Booking b = bookingDAO.getById(bookingId);
            if (b != null) {
                carDAO.updateCarStatus(b.getCarId(), "reserved");
            }
        }
        return ok;
    }

    public boolean startBooking(int bookingId) {
        boolean ok = bookingDAO.updateBookingStatus(bookingId, "active");
        if (ok) {
            Booking b = bookingDAO.getById(bookingId);
            if (b != null) {
                carDAO.updateCarStatus(b.getCarId(), "rented");
            }
        }
        return ok;
    }

    public boolean completeBooking(int bookingId) {
        boolean ok = bookingDAO.updateBookingStatus(bookingId, "completed");
        if (ok) {
            Booking b = bookingDAO.getById(bookingId);
            if (b != null) {
                carDAO.updateCarStatus(b.getCarId(), "available");
            }
        }
        return ok;
    }

    public boolean cancelBooking(int bookingId) {
        boolean ok = bookingDAO.updateBookingStatus(bookingId, "cancelled");
        if (ok) {
            Booking b = bookingDAO.getById(bookingId);
            if (b != null) {
                carDAO.updateCarStatus(b.getCarId(), "available");
            }
        }
        return ok;
    }
}
