package cinema;

public class Showtime {
    private int id;
    private int movieId;
    private int hallId;
    private String startTime;
    private double price;

    public Showtime(int id, int movieId, int hallId, String startTime, double price) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getHallId() {
        return hallId;
    }

    public String getStartTime() {
        return startTime;
    }

    public double getPrice() {
        return price;
    }
}