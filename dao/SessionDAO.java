package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;
import models.UserSession;
import utils.DatabaseConnection;

public class SessionDAO {

    public String createSession(int userId) {
        // Generate unique session token
        String sessionToken = UUID.randomUUID().toString();

        // First, deactivate any existing active sessions for this user
        deactivateUserSessions(userId);

        String sql = "INSERT INTO user_sessions (user_id, session_token, created_at, last_accessed, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, userId);
            stmt.setString(2, sessionToken);
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setBoolean(5, true);

            if (stmt.executeUpdate() == 1) {
                return sessionToken;
            }
        } catch (SQLException e) {
            System.err.println("Database error creating session: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public UserSession getActiveSession() {
        String sql = "SELECT * FROM user_sessions WHERE is_active = TRUE ORDER BY last_accessed DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new UserSession(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("session_token"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("last_accessed").toLocalDateTime(),
                        rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error getting active session: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public UserSession getSessionByToken(String sessionToken) {
        String sql = "SELECT * FROM user_sessions WHERE session_token = ? AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionToken);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserSession(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("session_token"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("last_accessed").toLocalDateTime(),
                        rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error getting session by token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSessionAccess(String sessionToken) {
        String sql = "UPDATE user_sessions SET last_accessed = ? WHERE session_token = ? AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, sessionToken);

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating session access: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivateSession(String sessionToken) {
        String sql = "UPDATE user_sessions SET is_active = FALSE WHERE session_token = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionToken);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error deactivating session: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivateUserSessions(int userId) {
        String sql = "UPDATE user_sessions SET is_active = FALSE WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0; // Could be 0 if no sessions exist
        } catch (SQLException e) {
            System.err.println("Database error deactivating user sessions: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void cleanupExpiredSessions() {
        // Remove sessions older than 30 days
        String sql = "DELETE FROM user_sessions WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                System.out.println("Cleaned up " + deleted + " expired sessions");
            }
        } catch (SQLException e) {
            System.err.println("Database error cleaning up sessions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
