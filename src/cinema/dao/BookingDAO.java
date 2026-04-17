// Package declaration
package cinema.dao;
// Import database connection
import cinema.database.DatabaseConnection;
// Import standard Java libraries for SQL operations and collections
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * BookingDAO handles all database operations related to ticket bookings.
 * It provides methods to check seat availability and to insert new bookings.
 */
public class BookingDAO {

    /**
     * Checks if any of the given seat IDs are already booked for a specific screening.
     *
     * @param screeningId ID of the screening (showtime)
     * @param seatIds List of seat IDs to check
     * @return true if any seat is already booked, false otherwise
     *
     * Note: SQLExceptions are caught internally. If a database error occurs,
     * this method returns true to prevent double-booking.
     */
    public boolean areSeatsBooked(int screeningId, List<Integer> seatIds) {
        // SQL query to check if a seat is booked
        String checkSQL = "SELECT COUNT(*) FROM tickets WHERE screening_id = ? AND seat_id = ? AND status = 'BOOKED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {

            // Iterate through all seat IDs to see if any are booked
            for (int seatId : seatIds) {
                checkStmt.setInt(1, screeningId);
                checkStmt.setInt(2, seatId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true; // Found a seat that is already booked
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Log exception internally
            return true; // If DB fails, assume booked to be safe
        }

        return false; // All seats are available
    }

    /**
     * Inserts booking records for a given user and a list of seat IDs.
     *
     * @param userId ID of the user making the booking
     * @param screeningId ID of the screening (showtime)
     * @param seatIds List of seat IDs to book
     * @return true if all inserts succeeded, false otherwise
     *
     * Note: SQLExceptions are caught internally. Each seat is inserted individually.
     */
    public boolean bookSeats(int userId, int screeningId, List<Integer> seatIds) {
        // SQL query to insert a booked ticket
        String insertSQL = "INSERT INTO tickets (user_id, screening_id, seat_id, status) VALUES (?, ?, ?, 'BOOKED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

            // Insert each seat one by one
            for (int seatId : seatIds) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, screeningId);
                insertStmt.setInt(3, seatId);

                int affected = insertStmt.executeUpdate();
                if (affected == 0) return false; // Failed to insert this seat
            }

            return true; // All inserts succeeded
        } catch (Exception ex) {
            ex.printStackTrace(); // Log exception internally
            return false; // Return false if any DB error occurs
        }
    }
}