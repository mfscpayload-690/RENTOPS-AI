package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.Booking;
import utils.DatabaseConnection;

public class BookingDAO {
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("car_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBigDecimal("total_price"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error getting bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("car_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBigDecimal("total_price"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error getting user bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    public Booking getById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("car_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBigDecimal("total_price"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error getting booking by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, status, total_price, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCarId());
            stmt.setDate(3, Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, Date.valueOf(booking.getEndDate()));
            stmt.setString(5, booking.getStatus());
            stmt.setBigDecimal(6, booking.getTotalPrice());
            stmt.setTimestamp(7, Timestamp.valueOf(booking.getCreatedAt()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error creating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET user_id = ?, car_id = ?, start_date = ?, end_date = ?, status = ?, total_price = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCarId());
            stmt.setDate(3, Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, Date.valueOf(booking.getEndDate()));
            stmt.setString(5, booking.getStatus());
            stmt.setBigDecimal(6, booking.getTotalPrice());
            stmt.setInt(7, booking.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating booking status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isCarAvailable(int carId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE car_id = ? AND status IN ('confirmed', 'active') AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) OR (start_date >= ? AND end_date <= ?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setDate(6, Date.valueOf(startDate));
            stmt.setDate(7, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error checking car availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}