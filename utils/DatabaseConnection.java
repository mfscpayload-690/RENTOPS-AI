package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_PATH = "config/db.properties";

    private DatabaseConnection() {
    }

    // Returns a NEW connection each time; callers should close it (try-with-resources).
    // This avoids issues from sharing a single static Connection across threads and DAOs.
    public static Connection getConnection() throws SQLException {
        try {
            // Ensure MySQL JDBC Driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
                props.load(fis);
            }
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            throw new SQLException("Failed to load DB config", e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}
