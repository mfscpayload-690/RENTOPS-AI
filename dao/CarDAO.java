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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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
        } catch (SQLException e) {
            System.err.println("Database error getting cars: " + e.getMessage());
            e.printStackTrace();
        }
        return cars;
    }
    
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE status = 'available' ORDER BY make, model";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
        } catch (SQLException e) {
            System.err.println("Database error getting available cars: " + e.getMessage());
            e.printStackTrace();
        }
        return cars;
    }
    
    public Car getById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
        } catch (SQLException e) {
            System.err.println("Database error getting car by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (make, model, year, license_plate, status, specs, price_per_day) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error adding car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, license_plate = ?, status = ?, specs = ?, price_per_day = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setString(4, car.getLicensePlate());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getSpecs());
            stmt.setBigDecimal(7, car.getPricePerDay());
            stmt.setInt(8, car.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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