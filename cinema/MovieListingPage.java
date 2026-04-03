package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.io.File; // Import the File class

public class MovieListingPage extends JFrame {
    private JPanel moviePanel;

    public MovieListingPage() {
        // Set the window title and basic settings
        setTitle("Movie Listings");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the movie panel
        moviePanel = new JPanel();
        moviePanel.setLayout(new GridLayout(0, 1, 10, 10));  // Grid layout with space between rows
        moviePanel.setBackground(Color.WHITE);

        // Scrollable area for the movie panel (if there are too many movies)
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch all movies from the database
        List<Movie> movies = getAllMovies();

        // Display movies in the panel
        for (Movie movie : movies) {
            JPanel movieCard = createMovieCard(movie);
            moviePanel.add(movieCard);
        }

        // Window settings
        setSize(1000, 750);
        setLocationRelativeTo(null);  // Center the window
    }

    // Fetch all movies from the database
    private List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM movies";  // Fetching all movies
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Create movie objects from the data
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int duration = rs.getInt("duration_minutes");
                double rating = rs.getDouble("rating");
                String releaseDate = rs.getString("release_date");
                String imagePath = rs.getString("image_path"); // Get image path

                movies.add(new Movie(id, title, description, duration, rating, releaseDate, imagePath));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        return movies;
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));  // Light gray background
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Movie Title
        JLabel titleLabel = new JLabel(movie.getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Movie Description (truncated if too long)
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setPreferredSize(new Dimension(600, 80));  // Limit description area size

        // Movie Duration, Rating, and Release Date
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(1, 3));  // 3 columns: duration, rating, release date

        JLabel durationLabel = new JLabel("Duration: " + movie.getDuration() + " min");
        JLabel ratingLabel = new JLabel("Rating: " + movie.getRating());
        JLabel releaseDateLabel = new JLabel("Released: " + movie.getReleaseDate());

        detailsPanel.add(durationLabel);
        detailsPanel.add(ratingLabel);
        detailsPanel.add(releaseDateLabel);

        // Movie Poster
        ImageIcon moviePoster = null;

        // Check if the image path is not null
        if (movie.getImagePath() != null && !movie.getImagePath().isEmpty()) {
            // Use the full path to the image
            File imageFile = new File("images/" + movie.getImagePath());
            if (imageFile.exists()) {
                // Load the image and scale it to desired size
               // System.out.println("Image exists: " + imageFile.getAbsolutePath());
                Image img = new ImageIcon(imageFile.getAbsolutePath()).getImage();
                Image scaledImg = img.getScaledInstance(200, 225, Image.SCALE_SMOOTH); // Resize image to 200x225 pixels
                moviePoster = new ImageIcon(scaledImg);
            } else {
                System.out.println("Image not found: " + imageFile.getAbsolutePath());
                // If the image is not found, use a default image and scale it
                Image img = new ImageIcon("images/default.jpg").getImage();
                Image scaledImg = img.getScaledInstance(200, 225, Image.SCALE_SMOOTH); // Resize default image to 200x225 pixels
                moviePoster = new ImageIcon(scaledImg);
            }
        } else {
            // Use a default image if the movie has no image path and scale it
            Image img = new ImageIcon("images/default.jpg").getImage();
            Image scaledImg = img.getScaledInstance(200, 225, Image.SCALE_SMOOTH); // Resize default image to 200x225 pixels
            moviePoster = new ImageIcon(scaledImg);
        }

        JLabel posterLabel = new JLabel(moviePoster);
        posterLabel.setPreferredSize(new Dimension(200, 225));  // Set size for the poster image

        // "Watch Showtimes" button
        JButton playButton = new JButton("Watch Showtimes");
        playButton.setFont(new Font("Arial", Font.BOLD, 16));
        playButton.setBackground(new Color(34, 150, 243));  // Blue button color
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openShowtimesPage(movie.getId());
                System.out.println("The WatchShowtimesPage executed successfully");

            }
        });

        // Putting all components together into the movie card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());

        // Adding image, title, and description in the card
        cardPanel.add(posterLabel, BorderLayout.WEST);
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);  // Add description with scroll
        cardPanel.add(detailsPanel, BorderLayout.SOUTH);
        cardPanel.add(playButton, BorderLayout.EAST);  // Play button at the side

        card.add(cardPanel, BorderLayout.CENTER);

        return card;
    }

    // Simulate watching the movie
    private void watchMovie(Movie movie) {
        JOptionPane.showMessageDialog(this, "Now playing: " + movie.getTitle());
        // You can open a new window or integrate a video player here
    }

    // Method to open the Watch Showtimes page
    private void openShowtimesPage(int movieId) {
        new WatchShowtimesPage(movieId).setVisible(true);  // Show the Watch Showtimes page
        this.dispose(); // Optionally close the current movie listing page
        System.out.println("WENT BACK TO Showtimes PAGE successfully");
    }

    public static void main(String[] args) {
        // Display movie listings
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MovieListingPage().setVisible(true);
                System.out.println("The MovieListingPage executed successfully");
            }
        });
    }
}