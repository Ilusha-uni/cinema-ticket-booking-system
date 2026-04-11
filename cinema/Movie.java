package cinema;

public class Movie {
    private int id;
    private String title;
    private String description;
    private int duration;
    private double rating;
    private String releaseDate;
    private String imagePath; // new field for image path

    // Constructor
    public Movie(int id, String title, String description, int duration, double rating, String releaseDate, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}