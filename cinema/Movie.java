package cinema;

// Represents a movie with details like title, description, duration, rating, release date, and image
public class Movie {
    private int id;                // Unique identifier for the movie
    private String title;          // Movie title
    private String description;    // Brief summary of the movie
    private int duration;          // Duration in minutes
    private double rating;         // Movie rating (e.g., 0.0 - 10.0)
    private String releaseDate;    // Release date as string
    private String imagePath;      // Path to movie poster/image

    // Constructor to initialize all movie attributes
    public Movie(int id, String title, String description, int duration, double rating, String releaseDate, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public double getRating() { return rating; }
    public String getReleaseDate() { return releaseDate; }
    public String getImagePath() { return imagePath; }

    // Setter
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
