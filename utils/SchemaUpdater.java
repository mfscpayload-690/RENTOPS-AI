package utils;

import java.sql.*;

public class SchemaUpdater {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connected to database");

            // Check if organization column exists
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "users", "organization");
            if (rs.next()) {
                System.out.println("Organization column already exists");
            } else {
                // Add organization column
                String sql = "ALTER TABLE users ADD COLUMN organization VARCHAR(100) AFTER role";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    System.out.println("Organization column added successfully");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
