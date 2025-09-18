package dao;

import java.sql.*;
import java.time.LocalDateTime;
import models.User;
import utils.DatabaseConnection;
import utils.PasswordHasher;

public class UserDAO {
    public boolean register(String username, String password, String role) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username cannot be empty");
            return false;
        }
        if (password == null || password.length() < 4) {
            System.err.println("Password must be at least 4 characters");
            return false;
        }
        if (role == null || (!role.equals("user") && !role.equals("admin"))) {
            System.err.println("Role must be 'user' or 'admin'");
            return false;
        }
        
        // Check if username already exists
        if (usernameExists(username)) {
            System.err.println("Username already exists");
            return false;
        }
        
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);
        String sql = "INSERT INTO users (username, password_hash, role, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, hash + ":" + salt);
            stmt.setString(3, role);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String[] hashSalt = rs.getString("password_hash").split(":");
                String hash = hashSalt[0];
                String salt = hashSalt[1];
                if (PasswordHasher.hash(password, salt).equals(hash)) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
