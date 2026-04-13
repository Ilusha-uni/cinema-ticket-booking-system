package cinema.database;
//Importing necessary classes from the java.sql package to handle database connectivity.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The DatabaseConnection class provides a centralized way to establish
 * and manage a connection to the MySQL database.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
    private static final String USER = "cinemadb";
    private static final String PASSWORD = "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@";

    // Establishes a connection to the database using the defined parameters.
    public static Connection getConnection() throws SQLException {
        try {
            // DriverManager.getConnection attempts to establish a connection to database
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException ex) {
            //If connection fails, show the error message and the stack trace
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace(); // Print stack trace for debugging purposes
            throw ex; // Re-throw the exception
        }
    }

    //Main method used for testing the database connection
    public static void main(String[] args) {
        try {
            // Attempt to trigger the connection logic
            getConnection();
            System.out.println("Connection test successful!");
        } catch (SQLException e) {
            // Error is already printed in the getConnection() method
        }
    }
}