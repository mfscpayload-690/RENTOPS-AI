package utils;

import dao.CarDAO;
import java.util.List;
import models.Car;

/**
 * A utility class to test car image URL functionality
 */
public class ImageUrlTester {

    public static void main(String[] args) {
        System.out.println("Testing image URL functionality...");

        // Sample image URLs for different car brands
        String[] sampleUrls = {
            "https://example.com/cars/toyota/gallery/",
            "https://example.com/cars/honda/civic_2023.jpg",
            "https://example.com/cars/tesla/model3/",
            "https://example.com/cars/ford/f150_exterior.jpg",
            "https://example.com/cars/bmw/x5_collection/"
        };

        // Update car image URLs in database
        try {
            CarDAO carDAO = new CarDAO();
            List<Car> allCars = carDAO.getAllCars();

            System.out.println("\nFound " + allCars.size() + " cars in database");

            // Assign URLs to cars
            int count = 0;
            for (Car car : allCars) {
                if (count < sampleUrls.length) {
                    String url = sampleUrls[count];
                    System.out.println("Setting URL for " + car.getMake() + " " + car.getModel() + ": " + url);

                    // Create a new car object with the URL for both exterior and interior
                    Car updatedCar = new Car(
                            car.getId(),
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            car.getLicensePlate(),
                            car.getStatus(),
                            car.getSpecs(),
                            car.getPricePerDay(),
                            car.getTotalKmDriven(),
                            url,
                            url // Use the same URL for both exterior and interior for testing
                    );

                    // Update in database
                    boolean success = carDAO.updateCar(updatedCar);
                    System.out.println("Update " + (success ? "successful" : "failed"));
                    count++;
                }
            }

            System.out.println("\nUpdated " + count + " cars with image URLs");

            // Verify URLs were set
            System.out.println("\nVerifying image URLs:");
            allCars = carDAO.getAllCars();
            for (Car car : allCars) {
                System.out.println(car.getMake() + " " + car.getModel() + ": "
                        + "\n   Exterior: " + (car.getExteriorImageUrl() != null ? car.getExteriorImageUrl() : "No URL")
                        + "\n   Interior: " + (car.getInteriorImageUrl() != null ? car.getInteriorImageUrl() : "No URL"));
            }

        } catch (Exception e) {
            System.err.println("Error testing image URLs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
