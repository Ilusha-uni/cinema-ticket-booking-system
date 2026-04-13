// Package declaration
package cinema.models;

/**
 * Represents a scheduled movie showing, including location, time, and pricing.
 */
public class Showtime {
    private int id;         // Unique identifier for this showtime
    private int movieId;    // ID of the movie being shown
    private int hallId;     // ID of the cinema hall where the show takes place
    private String startTime; // Start time (e.g., "18:30")
    private double price;   // Ticket price in local currency

    /**
     * Creates a new showtime with all required details.
     */
    public Showtime(int id, int movieId, int hallId, String startTime, double price) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.price = price;
    }

    // Getters
    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public int getHallId() { return hallId; }
    public String getStartTime() { return startTime; }
    public double getPrice() { return price; }
}
