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

            // Check if exterior_image_url column exists in cars table
            rs = meta.getColumns(null, null, "cars", "exterior_image_url");
            if (rs.next()) {
                System.out.println("Exterior image URL column already exists in cars table");
                // Try to widen column to 512 if smaller
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE cars MODIFY COLUMN exterior_image_url VARCHAR(512) DEFAULT NULL");
                    System.out.println("Exterior image URL column widened to 512 characters");
                } catch (SQLException ex) {
                    System.out.println("Skipping exterior_image_url widen: " + ex.getMessage());
                }
            } else {
                // Check if old image_url column exists and migrate data if needed
                boolean hasOldImageColumn = false;
                ResultSet oldImageRs = meta.getColumns(null, null, "cars", "image_url");
                if (oldImageRs.next()) {
                    hasOldImageColumn = true;
                }

                // Add exterior_image_url column
                String sql = "ALTER TABLE cars ADD COLUMN exterior_image_url VARCHAR(512) DEFAULT NULL";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    System.out.println("Exterior image URL column added successfully to cars table");

                    // Migrate data from old image_url column if it exists
                    if (hasOldImageColumn) {
                        stmt.executeUpdate("UPDATE cars SET exterior_image_url = image_url WHERE image_url IS NOT NULL");
                        System.out.println("Migrated data from image_url to exterior_image_url");
                    }
                }
            }

            // Check if interior_image_url column exists in cars table
            rs = meta.getColumns(null, null, "cars", "interior_image_url");
            if (rs.next()) {
                System.out.println("Interior image URL column already exists in cars table");
                // Try to widen column to 512 if smaller
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE cars MODIFY COLUMN interior_image_url VARCHAR(512) DEFAULT NULL");
                    System.out.println("Interior image URL column widened to 512 characters");
                } catch (SQLException ex) {
                    System.out.println("Skipping interior_image_url widen: " + ex.getMessage());
                }
            } else {
                // Add interior_image_url column
                String sql = "ALTER TABLE cars ADD COLUMN interior_image_url VARCHAR(512) DEFAULT NULL";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    System.out.println("Interior image URL column added successfully to cars table");

                    // Check if old image_url column exists and migrate data if needed
                    ResultSet oldImageRs = meta.getColumns(null, null, "cars", "image_url");
                    if (oldImageRs.next()) {
                        stmt.executeUpdate("UPDATE cars SET interior_image_url = image_url WHERE image_url IS NOT NULL");
                        System.out.println("Migrated data from image_url to interior_image_url");
                    }
                }
            }

            // Check if user_sessions table exists
            rs = meta.getTables(null, null, "user_sessions", null);
            if (rs.next()) {
                System.out.println("User sessions table already exists");
            } else {
                // Create user_sessions table
                String createSessionsTable = """
                    CREATE TABLE user_sessions (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NOT NULL,
                        session_token VARCHAR(255) UNIQUE NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        is_active BOOLEAN DEFAULT TRUE,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    )
                    """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(createSessionsTable);
                    System.out.println("User sessions table created successfully");
                }

                // Create indexes
                String createIndexes = """
                    CREATE INDEX idx_session_token ON user_sessions(session_token);
                    CREATE INDEX idx_user_active_sessions ON user_sessions(user_id, is_active);
                    """;
                try (Statement stmt = conn.createStatement()) {
                    for (String indexSql : createIndexes.split(";")) {
                        if (!indexSql.trim().isEmpty()) {
                            stmt.executeUpdate(indexSql.trim());
                        }
                    }
                    System.out.println("Session table indexes created successfully");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
