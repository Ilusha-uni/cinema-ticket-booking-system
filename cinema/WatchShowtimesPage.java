package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.io.File; // Import the File class

public class WatchShowtimesPage extends JFrame {
    private JPanel showtimePanel;
    private int movieId;

    public WatchShowtimesPage(int movieId) {
        this.movieId = movieId;
        setTitle("Watch Showtimes");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a Back Button to go back to the Movie Listing page
        JButton backButton = new JButton("Back to Movies");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(200, 0, 0));  // Red button color for "Go Back"
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 40));  // Button size
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMovies();
            }
        });

        // Add the back button to the top of the frame (for example, in a JPanel)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Initialize the showtimes panel
        showtimePanel = new JPanel();
        showtimePanel.setLayout(new GridLayout(0, 1, 10, 10));  // Grid layout with space between rows
        showtimePanel.setBackground(Color.WHITE);

        // Fetch and display movie details at the top of the page
        Movie movie = getMovieDetails(movieId);
        JPanel movieDetailsPanel = createMovieDetailsPanel(movie);
        add(movieDetailsPanel, BorderLayout.CENTER);

        // Scrollable area for the showtime panel (if there are too many showtimes)
        JScrollPane scrollPane = new JScrollPane(showtimePanel);
        add(scrollPane, BorderLayout.SOUTH);

        // Fetch and display all showtimes for the selected movie
        List<Showtime> showtimes = getShowtimesForMovie(movieId);
        for (Showtime showtime : showtimes) {
            JPanel showtimeCard = createShowtimeCard(showtime);
            showtimePanel.add(showtimeCard);
        }

        // Window settings
        setSize(1000, 800);
        setLocationRelativeTo(null);  // Center the window
    }

    // Fetch movie details from the database
    private Movie getMovieDetails(int movieId) {
        Movie movie = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM movies WHERE id = ?";  // Fetching movie details
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int duration = rs.getInt("duration_minutes");
                double rating = rs.getDouble("rating");
                String releaseDate = rs.getString("release_date");
                String imagePath = rs.getString("image_path");

                movie = new Movie(id, title, description, duration, rating, releaseDate, imagePath);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching movie details: " + ex.getMessage());
        }

        return movie;
    }

    // Fetch showtimes for a movie from the database
    private List<Showtime> getShowtimesForMovie(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM screenings WHERE movie_id = ?";  // Fetching showtimes for the movie
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int hallId = rs.getInt("hall_id");
                String startTime = rs.getString("start_time");
                double price = rs.getDouble("price");

                showtimes.add(new Showtime(id, movieId, hallId, startTime, price));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        return showtimes;
    }

    // Create a panel displaying all the movie details
    private JPanel createMovieDetailsPanel(Movie movie) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Movie title
        JLabel titleLabel = new JLabel(movie.getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Movie poster (image)
        ImageIcon moviePoster = loadImage(movie.getImagePath());
        JLabel posterLabel = new JLabel(moviePoster);
        posterLabel.setPreferredSize(new Dimension(200, 300));  // Set size for the poster image

        // Movie description
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setPreferredSize(new Dimension(600, 100));  // Limit description area size
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

        // Movie other details (duration, rating, release date)
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3));
        detailsPanel.setBackground(Color.WHITE);

        JLabel durationLabel = new JLabel("Duration: " + movie.getDuration() + " min");
        JLabel ratingLabel = new JLabel("Rating: " + movie.getRating());
        JLabel releaseDateLabel = new JLabel("Released: " + movie.getReleaseDate());

        detailsPanel.add(durationLabel);
        detailsPanel.add(ratingLabel);
        detailsPanel.add(releaseDateLabel);

        // Add all components to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(posterLabel, BorderLayout.WEST);
        panel.add(descriptionScrollPane, BorderLayout.CENTER);
        panel.add(detailsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Load image from the file system or use a default image
    private ImageIcon loadImage(String imagePath) {
        ImageIcon moviePoster = null;

        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File("images/" + imagePath);
            if (imageFile.exists()) {
                moviePoster = new ImageIcon(imageFile.getAbsolutePath());
            } else {
                moviePoster = new ImageIcon("images/default.jpg");
            }
        } else {
            moviePoster = new ImageIcon("images/default.jpg");
        }

        Image img = moviePoster.getImage();
        Image scaledImg = img.getScaledInstance(200, 300, Image.SCALE_SMOOTH);  // Resize to preferred size
        return new ImageIcon(scaledImg);
    }

    // Create a JPanel for each showtime with its details
    private JPanel createShowtimeCard(Showtime showtime) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));  // Light gray background
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setPreferredSize(new Dimension(700, 150));

        // Showtime details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());  // Use GridBagLayout for better control
        detailsPanel.setBackground(Color.WHITE);

        // Create GridBag constraints for better control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some space between components

        // Start Time label
        JLabel startTimeLabel = new JLabel("Start Time: " + showtime.getStartTime());
        startTimeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(startTimeLabel, gbc);

        // Price label
        JLabel priceLabel = new JLabel("Price: €" + showtime.getPrice());
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        detailsPanel.add(priceLabel, gbc);

        // Hall label
        JLabel hallLabel = new JLabel("Hall: " + showtime.getHallId());
        hallLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(hallLabel, gbc);

        // "Select Seat" button
        JButton selectSeatButton = new JButton("Select Seat");
        selectSeatButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectSeatButton.setBackground(new Color(34, 150, 243));  // Blue button color
        selectSeatButton.setForeground(Color.WHITE);
        selectSeatButton.setFocusPainted(false);
        selectSeatButton.setPreferredSize(new Dimension(150, 40));  // Size the button
        selectSeatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSeat(showtime);  // Open Seat Selection Page
            }
        });

        // Place the button at the bottom of the card
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  // Align button to the right
        buttonPanel.add(selectSeatButton);

        // Add components to the card
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void selectSeat(Showtime showtime) {
        new SeatSelectionPage(showtime.getHallId(), showtime.getMovieId()).setVisible(true);  // Pass movieId
        this.dispose(); // Close current showtimes page
        System.out.println("CONNECTED TO SEAT PAGE successfully");
    }


    // Method to go back to the MovieListingPage
    private void backToMovies() {
        new MovieListingPage().setVisible(true); // Open the Movie Listing page
        this.dispose(); // Close the current Watch Showtimes page
        System.out.println("WENT BACK TO MOVIES PAGE successfully");
    }

    public static void main(String[] args) {
        // Display the showtimes page for a specific movie (for example, Movie ID 1)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WatchShowtimesPage(1).setVisible(true);
            }
        });
    }
}