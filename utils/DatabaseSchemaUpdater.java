package utils;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSchemaUpdater {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {

            System.out.println("Updating database schema for multi-image support...");

            // Check if columns already exist
            try {
                stmt.executeQuery("SELECT exterior_images, interior_images FROM cars LIMIT 1");
                System.out.println("Image columns already exist. No update needed.");
                return;
            } catch (Exception e) {
                // Columns don't exist, proceed with adding them
                System.out.println("Adding new image columns...");
            }

            // Add exterior images column
            stmt.executeUpdate("ALTER TABLE cars ADD COLUMN exterior_images TEXT");
            System.out.println("✓ Added exterior_images column");

            // Add interior images column
            stmt.executeUpdate("ALTER TABLE cars ADD COLUMN interior_images TEXT");
            System.out.println("✓ Added interior_images column");

            // Add comments for documentation
            stmt.executeUpdate("ALTER TABLE cars MODIFY COLUMN exterior_images TEXT COMMENT 'JSON array of exterior image file paths'");
            stmt.executeUpdate("ALTER TABLE cars MODIFY COLUMN interior_images TEXT COMMENT 'JSON array of interior image file paths'");
            System.out.println("✓ Added column comments");

            System.out.println("Database schema update completed successfully!");

        } catch (Exception e) {
            System.err.println("Database schema update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
