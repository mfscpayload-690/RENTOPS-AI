package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Car;
import utils.DatabaseConnection;

public class CarDAO {

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY make, model";
        System.out.println("CarDAO: Executing getAllCars query");
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
                System.out.println("CarDAO: total_km_driven column exists: " + hasKmDrivenColumn);
            } catch (Exception e) {
                System.err.println("CarDAO: Error checking columns: " + e.getMessage());
            }

            int count = 0;
            while (rs.next()) {
                count++;
                try {
                    // Try to get image URLs if they exist
                    String exteriorImageUrl = null;
                    String interiorImageUrl = null;

                    // Try exterior_image_url first
                    try {
                        exteriorImageUrl = rs.getString("exterior_image_url");
                    } catch (SQLException e) {
                        // Try the old image_url as fallback
                        try {
                            exteriorImageUrl = rs.getString("image_url");
                        } catch (SQLException ex) {
                            // Neither column exists, that's okay
                        }
                    }

                    // Try interior_image_url
                    try {
                        interiorImageUrl = rs.getString("interior_image_url");
                    } catch (SQLException e) {
                        // Use exterior image as fallback for interior
                        interiorImageUrl = exteriorImageUrl;
                    }

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
                                rs.getInt("total_km_driven"),
                                exteriorImageUrl,
                                interiorImageUrl
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
                                rs.getBigDecimal("price_per_day"),
                                0,
                                exteriorImageUrl,
                                interiorImageUrl
                        ));
                    }
                } catch (SQLException ex) {
                    System.err.println("CarDAO: Error getting car data: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.out.println("CarDAO: Found " + count + " cars, added " + cars.size() + " to list");
        } catch (SQLException e) {
            System.err.println("Database error getting cars: " + e.getMessage());
            e.printStackTrace();
        }
        return cars;
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

                    // Try to get image URLs if they exist
                    String exteriorImageUrl = null;
                    String interiorImageUrl = null;

                    // Try exterior_image_url first
                    try {
                        exteriorImageUrl = rs.getString("exterior_image_url");
                    } catch (SQLException e) {
                        // Try the old image_url as fallback
                        try {
                            exteriorImageUrl = rs.getString("image_url");
                        } catch (SQLException ex) {
                            // Neither column exists, that's okay
                        }
                    }

                    // Try interior_image_url
                    try {
                        interiorImageUrl = rs.getString("interior_image_url");
                    } catch (SQLException e) {
                        // Use exterior image as fallback for interior
                        interiorImageUrl = exteriorImageUrl;
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
                                rs.getInt("total_km_driven"),
                                exteriorImageUrl,
                                interiorImageUrl
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
                                rs.getBigDecimal("price_per_day"),
                                0,
                                exteriorImageUrl,
                                interiorImageUrl
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
        String sql = "INSERT INTO cars (make, model, year, license_plate, status, specs, price_per_day, total_km_driven, exterior_image_url, interior_image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            stmt.setInt(8, car.getTotalKmDriven());
            stmt.setString(9, car.getExteriorImageUrl());
            stmt.setString(10, car.getInteriorImageUrl());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error adding car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, license_plate = ?, status = ?, specs = ?, price_per_day = ?, total_km_driven = ?, exterior_image_url = ?, interior_image_url = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            stmt.setInt(8, car.getTotalKmDriven());
            stmt.setString(9, car.getExteriorImageUrl());
            stmt.setString(10, car.getInteriorImageUrl());
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
}
