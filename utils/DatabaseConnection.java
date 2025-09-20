package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_PATH = "config/db.properties";

    // Cached config
    private static final Properties DB_PROPS = new Properties();
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            DB_PROPS.load(fis);
            URL = DB_PROPS.getProperty("db.url");
            USER = DB_PROPS.getProperty("db.user");
            PASSWORD = DB_PROPS.getProperty("db.password");
            // Load driver once
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load DB config: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
    }

    // Return a new connection each time; config and driver are cached.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
