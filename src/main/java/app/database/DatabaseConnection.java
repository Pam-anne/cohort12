package app.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static Connection connection;
    private static Properties properties;

    private DatabaseConnection() {
    }

    public static synchronized void init() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        properties = loadProperties();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found on classpath", e);
        }

        connection = DriverManager.getConnection(
                jdbcUrl(properties.getProperty("db.name")),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));

        System.out.println("[DatabaseConnection] Singleton connection opened for database: "
                + properties.getProperty("db.name"));
    }

    public static synchronized Connection get() {
        if (connection == null) {
            throw new IllegalStateException(
                    "DatabaseConnection has not been initialized. "
                            + "Ensure DatabaseInitializer ServletContextListener ran on startup.");
        }
        return connection;
    }

    public static synchronized Properties getProperties() {
        if (properties == null) {
            properties = loadProperties();
        }
        return properties;
    }

    public static synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatabaseConnection] Singleton connection closed.");
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public static String jdbcUrl(String databaseName) {
        Properties p = getProperties();
        return "jdbc:postgresql://" + p.getProperty("db.host")
                + ":" + p.getProperty("db.port")
                + "/" + databaseName;
    }

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException(PROPERTIES_FILE + " not found on classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + PROPERTIES_FILE, e);
        }
        return p;
    }
}
