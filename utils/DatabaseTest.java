package utils;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                System.out.println("Database URL: " + conn.getMetaData().getURL());
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("✗ Database connection failed - connection is null or closed");
            }
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed:");
            System.out.println("Error: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());

            System.out.println("\nTroubleshooting tips:");
            System.out.println("1. Make sure MySQL/MariaDB server is running");
            System.out.println("2. Check if database 'rentops_ai' exists");
            System.out.println("3. Verify credentials in config/db.properties");
            System.out.println("4. Check if port 3306 is accessible");
        }
    }
}
