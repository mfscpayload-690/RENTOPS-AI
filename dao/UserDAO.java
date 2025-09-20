package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import models.User;
import utils.DatabaseConnection;
import utils.PasswordHasher;

public class UserDAO {

    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    public boolean register(String username, String password, String role, String organization) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username cannot be empty";
            return false;
        }
        if (password == null || password.length() < 4) {
            lastError = "Password must be at least 4 characters";
            return false;
        }
        if (role == null || (!role.equals("user") && !role.equals("admin"))) {
            lastError = "Role must be 'user' or 'admin'";
            return false;
        }

        // Check if username already exists
        if (usernameExists(username)) {
            lastError = "Username already exists";
            return false;
        }

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);
        String sql = "INSERT INTO users (username, password_hash, role, organization, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, hash + ":" + salt);
            stmt.setString(3, role);
            stmt.setString(4, organization != null ? organization.trim() : null);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            boolean result = stmt.executeUpdate() == 1;
            if (result) {
                lastError = "";
            } else {
                lastError = "Registration failed";
            }
            return result;
        } catch (SQLException e) {
            lastError = "Database error during registration: " + e.getMessage();
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Backward compatibility method
    public boolean register(String username, String password, String role) {
        return register(username, password, role, null);
    }

    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                            rs.getString("organization"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            lastError = "Database error during login: " + e.getMessage();
            System.err.println("Database error during login: " + e.getMessage());
        }
        lastError = "Invalid username or password";
        return null;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("organization"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            lastError = "Database error retrieving user: " + e.getMessage();
            System.err.println("Database error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = utils.DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("organization"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error getting users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
}
