package cinema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
    private static final String USER = "cinemadb"; // replace with your username
    private static final String PASSWORD = "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@"; // replace with your password

    // Method to get a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            //System.out.println("Attempting to connect to MySQL...");
            // Connecting to the database
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            //System.out.println("Successfully connected to MySQL!");
            return conn;
        } catch (SQLException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace(); // Prints the full stack trace of the error
            throw ex;
        }
    }

    public static void main(String[] args) {
        try {
            getConnection(); // Trying to connect
        } catch (SQLException e) {
            // Handling connection error
        }
    }
}