// Package declaration
package cinema

// Represents a movie showing in a hall at a specific time
public class Showtime {
    private int id;         // Showtime ID
    private int movieId;    // Associated movie ID
    private int hallId;     // Hall ID where the movie is shown
    private String startTime; // Start time of the show
    private double price;   // Ticket price

    // Initialize a showtime with all details
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
