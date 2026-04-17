// Package declaration
package cinema.models;

/**
 * Represents a scheduled movie showing in a specific cinema hall.
 * Contains details such as movie ID, hall ID, start time, and ticket price.
 */
public class Showtime {

    /** Unique identifier for the showtime */
    private int id;

    /** ID of the movie being shown */
    private int movieId;

    /** ID of the cinema hall where the show takes place */
    private int hallId;

    /** Start time of the show (e.g., "18:30") */
    private String startTime;

    /** Ticket price for this showtime */
    private double price;


    /**
     * Constructs a new Showtime with all required details.
     *
     * @param id Unique showtime identifier
     * @param movieId ID of the movie being shown
     * @param hallId ID of the hall
     * @param startTime Start time of the show
     * @param price Ticket price
     */
    public Showtime(int id, int movieId, int hallId, String startTime, double price) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.price = price;
    }

    // Getters

    /** @return the showtime ID */
    public int getId() { return id; }

    /** @return the movie ID for this showtime */
    public int getMovieId() { return movieId; }

    /** @return the hall ID for this showtime */
    public int getHallId() { return hallId; }

    /** @return the start time of the show */
    public String getStartTime() { return startTime; }

    /** @return the ticket price */
    public double getPrice() { return price; }


    /**
     * Returns a string representation of the showtime, including movie, hall, and start time.
     *
     * @return string describing the showtime
     */
    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", hallId=" + hallId +
                ", startTime='" + startTime + '\'' +
                ", price=" + price +
                '}';
    }
}
