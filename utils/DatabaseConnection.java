package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static Connection connection;
    private static final String CONFIG_PATH = "config/db.properties";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(CONFIG_PATH));
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException e) {
                throw new SQLException("Failed to load DB config", e);
            }
        }
        return connection;
    }
}
