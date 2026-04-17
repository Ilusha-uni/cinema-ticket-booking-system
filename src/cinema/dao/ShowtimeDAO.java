// Package declaration
package cinema.dao;
// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.models.Showtime;
// Import standard Java libraries for SQL operations and collections
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ShowtimeDAO handles all database operations related to movie screenings.
 * Provides methods to fetch showtimes by movie or by screening ID.
 */
public class ShowtimeDAO {

    /**
     * Retrieves all showtimes for a specific movie, ordered by start time.
     *
     * @param movieId ID of the movie
     * @return List of Showtime objects
     */
    public List<Showtime> getShowtimesByMovieId(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();

        String query = "SELECT * FROM screenings WHERE movie_id = ? ORDER BY start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                showtimes.add(new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getInt("hall_id"),
                        rs.getString("start_time"),
                        rs.getDouble("price")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log errors internally
        }

        return showtimes;
    }

    /**
     * Retrieves a specific showtime by its screening ID.
     *
     * @param screeningId ID of the screening
     * @return Showtime object or null if not found
     */
    public Showtime getShowtimeById(int screeningId) {
        String query = "SELECT * FROM screenings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, screeningId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getInt("hall_id"),
                        rs.getString("start_time"),
                        rs.getDouble("price")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log errors internally
        }

        return null;
    }
}