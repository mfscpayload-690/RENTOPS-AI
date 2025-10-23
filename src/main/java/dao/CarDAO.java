package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Car;
import utils.DatabaseConnection;

public class CarDAO {

    public List<Car> getAllCars() {
        return getAllCarsWithImages();
    }

    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE status = 'available' ORDER BY make, model";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            // Force metadata initialization to avoid NPE in some driver versions
            rs.getMetaData();

            // Check if total_km_driven column exists
            boolean hasKmDrivenColumn = false;
            try {
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    if ("total_km_driven".equalsIgnoreCase(metaData.getColumnName(i))) {
                        hasKmDrivenColumn = true;
                        break;
                    }
                }
                System.out.println("CarDAO: total_km_driven column exists in getAvailableCars: " + hasKmDrivenColumn);
            } catch (Exception e) {
                System.err.println("CarDAO: Error checking columns: " + e.getMessage());
            }

            while (rs.next()) {
                // Use the constructor with or without total_km_driven based on column existence
                if (hasKmDrivenColumn) {
                    cars.add(new Car(
                            rs.getInt("id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getString("license_plate"),
                            rs.getString("status"),
                            rs.getString("specs"),
                            rs.getBigDecimal("price_per_day"),
                            rs.getInt("total_km_driven")
                    ));
                } else {
                    cars.add(new Car(
                            rs.getInt("id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getString("license_plate"),
                            rs.getString("status"),
                            rs.getString("specs"),
                            rs.getBigDecimal("price_per_day")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error getting available cars: " + e.getMessage());
            e.printStackTrace();
        }
        return cars;
    }

    public Car getById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Check if total_km_driven column exists
                    boolean hasKmDrivenColumn = false;
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            if ("total_km_driven".equalsIgnoreCase(metaData.getColumnName(i))) {
                                hasKmDrivenColumn = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("CarDAO: Error checking columns in getById: " + e.getMessage());
                    }

                    if (hasKmDrivenColumn) {
                        return new Car(
                                rs.getInt("id"),
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getInt("year"),
                                rs.getString("license_plate"),
                                rs.getString("status"),
                                rs.getString("specs"),
                                rs.getBigDecimal("price_per_day"),
                                rs.getInt("total_km_driven")
                        );
                    } else {
                        return new Car(
                                rs.getInt("id"),
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getInt("year"),
                                rs.getString("license_plate"),
                                rs.getString("status"),
                                rs.getString("specs"),
                                rs.getBigDecimal("price_per_day")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error getting car by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (make, model, year, license_plate, status, specs, price_per_day, total_km_driven, exterior_images, interior_images) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            stmt.setInt(8, car.getTotalKmDriven());
            stmt.setString(9, listToJson(car.getExteriorImages()));
            stmt.setString(10, listToJson(car.getInteriorImages()));
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                // Retrieve and set the generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        car.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Database error adding car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, license_plate = ?, status = ?, specs = ?, price_per_day = ?, total_km_driven = ?, exterior_images = ?, interior_images = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            stmt.setInt(8, car.getTotalKmDriven());
            stmt.setString(9, listToJson(car.getExteriorImages()));
            stmt.setString(10, listToJson(car.getInteriorImages()));
            stmt.setInt(11, car.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error deleting car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, carId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== MULTI-IMAGE SUPPORT METHODS ====================
    /**
     * Converts a list of image paths to JSON string for database storage
     */
    private String listToJson(List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < imagePaths.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(imagePaths.get(i).replace("\"", "\\\"")).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Converts JSON string from database to list of image paths
     */
    private List<String> jsonToList(String json) {
        List<String> imagePaths = new ArrayList<>();
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) {
            return imagePaths;
        }

        try {
            // Simple JSON parsing for array of strings
            String cleanJson = json.trim();
            if (cleanJson.startsWith("[") && cleanJson.endsWith("]")) {
                cleanJson = cleanJson.substring(1, cleanJson.length() - 1);
                if (!cleanJson.trim().isEmpty()) {
                    String[] parts = cleanJson.split(",");
                    for (String part : parts) {
                        String path = part.trim();
                        if (path.startsWith("\"") && path.endsWith("\"")) {
                            path = path.substring(1, path.length() - 1);
                            path = path.replace("\\\"", "\"");
                            imagePaths.add(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON image paths: " + e.getMessage());
        }

        return imagePaths;
    }

    /**
     * Checks if image columns exist in the database
     */
    private boolean checkImageColumnsExist(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "cars", null);
            boolean hasExterior = false, hasInterior = false;

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                if ("exterior_images".equalsIgnoreCase(columnName)) {
                    hasExterior = true;
                } else if ("interior_images".equalsIgnoreCase(columnName)) {
                    hasInterior = true;
                }
            }
            return hasExterior && hasInterior;
        } catch (SQLException e) {
            System.err.println("Error checking image columns: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads car with image data from result set
     */
    private Car loadCarWithImages(ResultSet rs, boolean hasKmDrivenColumn, boolean hasImageColumns) throws SQLException {
        Car car;

        // Create car object based on available columns
        if (hasKmDrivenColumn) {
            car = new Car(
                    rs.getInt("id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getString("status"),
                    rs.getString("specs"),
                    rs.getBigDecimal("price_per_day"),
                    rs.getInt("total_km_driven")
            );
        } else {
            car = new Car(
                    rs.getInt("id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getString("status"),
                    rs.getString("specs"),
                    rs.getBigDecimal("price_per_day")
            );
        }

        // Load image data if columns exist
        if (hasImageColumns) {
            try {
                String exteriorJson = rs.getString("exterior_images");
                String interiorJson = rs.getString("interior_images");

                car.setExteriorImages(jsonToList(exteriorJson));
                car.setInteriorImages(jsonToList(interiorJson));
            } catch (SQLException e) {
                System.err.println("Error loading image data for car " + car.getId() + ": " + e.getMessage());
                // Continue without image data
            }
        }

        return car;
    }

    /**
     * Updates car images in the database
     */
    public boolean updateCarImages(int carId, List<String> exteriorImages, List<String> interiorImages) {
        String sql = "UPDATE cars SET exterior_images = ?, interior_images = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if image columns exist
            if (!checkImageColumnsExist(conn)) {
                System.err.println("Image columns do not exist. Please run database update script.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, listToJson(exteriorImages));
                stmt.setString(2, listToJson(interiorImages));
                stmt.setInt(3, carId);

                return stmt.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Database error updating car images: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Enhanced version of getAllCars that includes image data
     */
    public List<Car> getAllCarsWithImages() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY make, model";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            rs.getMetaData(); // Force metadata initialization

            // Check column existence
            boolean hasKmDrivenColumn = columnExists(rs, "total_km_driven");
            boolean hasImageColumns = checkImageColumnsExist(conn);

            System.out.println("CarDAO: Loading cars with images - KM column: " + hasKmDrivenColumn + ", Image columns: " + hasImageColumns);

            while (rs.next()) {
                try {
                    Car car = loadCarWithImages(rs, hasKmDrivenColumn, hasImageColumns);
                    cars.add(car);
                } catch (SQLException ex) {
                    System.err.println("CarDAO: Error loading car data: " + ex.getMessage());
                }
            }

            System.out.println("CarDAO: Loaded " + cars.size() + " cars with image data");
        } catch (SQLException e) {
            System.err.println("Database error getting cars with images: " + e.getMessage());
            e.printStackTrace();
        }

        return cars;
    }

    /**
     * Helper method to check if a column exists in the result set
     */
    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking column existence: " + e.getMessage());
        }
        return false;
    }
}
