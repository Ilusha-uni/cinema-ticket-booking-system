// Package declaration
package cinema.dao;
// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.models.Seat;
// Import standard Java libraries for SQL operations and collections
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SeatDAO handles all database operations related to seats.
 * It allows fetching seats for a specific hall and screening,
 * including their availability status.
 */
public class SeatDAO {

    /**
     * Retrieves all seats for a specific hall and screening.
     * Each seat includes its row, number, type, and whether it is booked.
     *
     * @param hallId      ID of the hall
     * @param screeningId ID of the screening (showtime)
     * @return List of Seat objects representing all seats in the hall,
     *         with their booked status.
     */
    public List<Seat> getSeatsForHall(int hallId, int screeningId) {
        List<Seat> seats = new ArrayList<>();

        // SQL query:
        // - Fetch all seats in the given hall.
        // - Check if each seat is booked for the specific screening using a subquery.
        // - Return "BOOKED" or "AVAILABLE" as seat_status.
        // - Order seats by row and seat number for display purposes.
        String query = "SELECT s.id, s.row_number, s.seat_number, s.seat_type, " +
                "CASE WHEN EXISTS (SELECT 1 FROM tickets t WHERE t.seat_id = s.id AND t.screening_id = ? AND t.status = 'BOOKED') " +
                "THEN 'BOOKED' ELSE 'AVAILABLE' END AS seat_status " +
                "FROM seats s WHERE s.hall_id = ? ORDER BY s.row_number, s.seat_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            // Set parameters for prepared statement
            pst.setInt(1, screeningId);
            pst.setInt(2, hallId);

            // Execute query and process results
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("id"),                // Seat ID
                        hallId,                         // Hall ID
                        rs.getInt("row_number"),        // Row number
                        rs.getInt("seat_number"),       // Seat number
                        rs.getString("seat_type"),      // Seat type (e.g., Regular, VIP)
                        "BOOKED".equals(rs.getString("seat_status")) // Boolean booked status
                ));
            }
        } catch (SQLException e) {
            // Log exceptions for debugging
            e.printStackTrace();
        }
        return seats; // Return the list of seats, empty if none found or error
    }

}