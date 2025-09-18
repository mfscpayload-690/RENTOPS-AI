package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DatabaseConnection;

public class DatabaseCleanup {

    public static void main(String[] args) {
        System.out.println("Database Cleanup and Session Analysis");

        // 1. Check all users
        System.out.println("\n1. All users in database:");
        checkUsers();

        // 2. Check all sessions
        System.out.println("\n2. All sessions in database:");
        checkSessions();

        // 3. Clean up all sessions
        System.out.println("\n3. Cleaning up all sessions...");
        cleanupAllSessions();

        // 4. Verify cleanup
        System.out.println("\n4. Sessions after cleanup:");
        checkSessions();

        System.out.println("\nCleanup completed!");
    }

    private static void checkUsers() {
        String sql = "SELECT id, username, role, organization FROM users";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("User ID: " + rs.getInt("id")
                        + ", Username: " + rs.getString("username")
                        + ", Role: " + rs.getString("role")
                        + ", Organization: " + rs.getString("organization"));
            }
        } catch (SQLException e) {
            System.err.println("Error checking users: " + e.getMessage());
        }
    }

    private static void checkSessions() {
        String sql = "SELECT id, user_id, session_token, is_active, created_at, last_accessed FROM user_sessions";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Session ID: " + rs.getInt("id")
                        + ", User ID: " + rs.getInt("user_id")
                        + ", Token: " + rs.getString("session_token").substring(0, 8) + "..."
                        + ", Active: " + rs.getBoolean("is_active")
                        + ", Created: " + rs.getTimestamp("created_at")
                        + ", Last Accessed: " + rs.getTimestamp("last_accessed"));
            }
        } catch (SQLException e) {
            System.err.println("Error checking sessions: " + e.getMessage());
        }
    }

    private static void cleanupAllSessions() {
        String sql = "UPDATE user_sessions SET is_active = FALSE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            int updated = stmt.executeUpdate();
            System.out.println("Deactivated " + updated + " sessions");
        } catch (SQLException e) {
            System.err.println("Error cleaning up sessions: " + e.getMessage());
        }
    }
}
