package cinema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Utility class to manage database connections
public class DatabaseConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/cinema_db"; // DB URL
    private static final String USER = "cinemadb"; // DB username
    private static final String PASSWORD = "yourpassword"; // DB password

    // Get a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Connect to DB
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException ex) {
            // Handle connection error
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace(); // Print full stack trace
            throw ex;
        }
    }

    // Test DB connection
    public static void main(String[] args) {
        try {
            getConnection(); // Attempt connection
        } catch (SQLException e) {
            // Connection failed
        }
    }
}