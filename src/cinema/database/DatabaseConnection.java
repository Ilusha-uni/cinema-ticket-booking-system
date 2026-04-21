// Package declaration
package cinema.database;

// Import necessary classes from java.sql for database connectivity
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a centralized way to establish connections to a MySQL database.
 * It ensures consistent access to the database throughout the application.
 */
public class DatabaseConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
    private static final String USER = "cinemadb";
    private static final String PASSWORD = "yourdbpassword";

    /**
     * Establishes and returns a connection to the database.
     *
     * @return Connection object to the MySQL database
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Attempt to establish a connection using DriverManager
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException ex) {
            throw new SQLException("Database connection error", ex);
        }
    }
}


