// Package declaration
package cinema.models;

/**
 * Represents a movie with its associated attributes.
 * Contains details like title, description, duration, rating, release date, and poster image path.
 */
public class Movie {

    /** Unique identifier for the movie */
    private int id;

    /** Title of the movie */
    private String title;

    /** Brief description or synopsis of the movie */
    private String description;

    /** Duration of the movie in minutes */
    private int duration;

    /** Rating of the movie on a 0.0–10.0 scale */
    private double rating;

    /** Release date of the movie as a String (format can be YYYY-MM-DD) */
    private String releaseDate;

    /** File path or URL pointing to the movie poster image */
    private String imagePath;

    /**
     * Constructs a new Movie object with all attributes initialized.
     *
     * @param id Unique identifier of the movie
     * @param title Title of the movie
     * @param description Short description or synopsis
     * @param duration Duration in minutes
     * @param rating Rating on a 0–10 scale
     * @param releaseDate Release date as a String
     * @param imagePath File path or URL to the movie poster
     */
    public Movie(int id, String title, String description, int duration, double rating, String releaseDate, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
    }


    /** @return the unique movie ID */
    public int getId() { return id; }

    /** @return the movie title */
    public String getTitle() { return title; }

    /** @return the movie description */
    public String getDescription() { return description; }

    /** @return the duration in minutes */
    public int getDuration() { return duration; }

    /** @return the rating on a 0–10 scale */
    public double getRating() { return rating; }

    /** @return the release date as a string */
    public String getReleaseDate() { return releaseDate; }

    /** @return the poster image path or URL */
    public String getImagePath() { return imagePath; }

    /**
     * Updates the poster image path or URL.
     *
     * @param imagePath New file path or URL for the movie poster
     */
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    /**
     * Returns a string representation of the Movie object.
     * Useful for debugging or logging purposes.
     *
     * @return String describing all the movie's attributes
     */
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
