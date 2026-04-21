// Package declaration
package cinema.dao;
// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.models.Movie;
// Import standard Java libraries for SQL operations and collections
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations related to movies.
 * It allows fetching a single movie by ID or retrieving all movies.
 */
public class MovieDAO {

    /**
     * Retrieves a movie from the database by its unique ID.
     *
     * @param movieId The ID of the movie to fetch
     * @return A Movie object populated with data from the database, or null if not found or on error
     */
    public Movie getMovieById(int movieId) {
        String query = "SELECT * FROM movies WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            // Set the parameter for the prepared statement
            pst.setInt(1, movieId);

            // Execute query and get result
            ResultSet rs = pst.executeQuery();

            // If a record is found, create and return a Movie object
            if (rs.next()) {
                return new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration_minutes"),
                        rs.getDouble("rating"),
                        rs.getString("release_date"),
                        rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            // Log exception for debugging purposes
            e.printStackTrace();
        }
        // Return null if movie is not found or an error occurred
        return null;
    }

    /**
     * Retrieves all movies from the database.
     *
     * @return A list of Movie objects. Returns an empty list if no movies are found or on error.
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            // Iterate through the result set and create Movie objects for each record
            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration_minutes"),
                        rs.getDouble("rating"),
                        rs.getString("release_date"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            // Log exception for debugging purposes
            e.printStackTrace();
        }
        return movies;
    }

}
