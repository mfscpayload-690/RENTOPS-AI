package test;

import dao.CarDAO;
import java.math.BigDecimal;
import java.util.List;
import models.Car;

public class ImagePersistenceTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== Image Persistence Test ===");

            CarDAO dao = new CarDAO();

            // Create a test car
            Car testCar = new Car(0, "Test", "Model", 2023, "TEST123", "available", "Test specs", new BigDecimal("100.00"), 0);

            System.out.println("1. Adding test car to database...");
            boolean added = dao.addCar(testCar);
            System.out.println("Car added: " + added);

            // Get the car back to get its ID
            List<Car> cars = dao.getAllCars();
            Car savedCar = null;
            for (Car car : cars) {
                if ("TEST123".equals(car.getLicensePlate())) {
                    savedCar = car;
                    break;
                }
            }

            if (savedCar != null) {
                System.out.println("2. Found saved car with ID: " + savedCar.getId());

                // Test image directory creation
                System.out.println("3. Testing image directory creation...");
                utils.ImageUtils.createCarImageDirectories(savedCar.getId());

                // Check if directories were created
                java.io.File exteriorDir = new java.io.File("ui" + java.io.File.separator + "components"
                        + java.io.File.separator + "images" + java.io.File.separator + "cars"
                        + java.io.File.separator + savedCar.getId() + java.io.File.separator + "exterior");
                java.io.File interiorDir = new java.io.File("ui" + java.io.File.separator + "components"
                        + java.io.File.separator + "images" + java.io.File.separator + "cars"
                        + java.io.File.separator + savedCar.getId() + java.io.File.separator + "interior");

                System.out.println("Exterior directory exists: " + exteriorDir.exists());
                System.out.println("Interior directory exists: " + interiorDir.exists());

                // Clean up test car
                System.out.println("5. Cleaning up test car...");
                dao.deleteCar(savedCar.getId());

                System.out.println("=== Test completed successfully! ===");
            } else {
                System.out.println("ERROR: Could not find saved test car!");
            }

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
