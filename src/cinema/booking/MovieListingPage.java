// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.auth.LoginForm;
import cinema.auth.UserSession;
import cinema.database.DatabaseConnection;
import cinema.models.Movie;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
// and utility classes for data collection
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MovieListingPage Class
 * This class represents the main dashboard where users can view available movies.
 * It extends JFrame to create a top-level window.
 */
public class MovieListingPage extends JFrame {

    // Panel that holds the list of movie cards
    private JPanel moviePanel;

    // Constant colors for a consistent UI theme
    private static final Color NAV_BAR_COLOR = new Color(18, 18, 18); // Dark color for top nav
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243); // Primary action color
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); // Light grey background

    /**
     * Constructor for the MovieListingPage
     * Initializes the window settings and builds the UI components.
     */
    public MovieListingPage() {
        // Set the window title
        setTitle("Movie Listings");
        // Ensure the application exits when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set initial window dimensions
        setSize(1300, 800);
        // Center the window on the screen
        setLocationRelativeTo(null);

        // Set the background color of the main content pane
        getContentPane().setBackground(BACKGROUND_COLOR);

        // --- Top Black Navigation Bar ---
        // Create a panel for the logout button at the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        // Initialize logout button with styling
        JButton logoutBtn = new JButton("← Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(NAV_BAR_COLOR);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Action listener to handle logout logic
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn);
        // Add the top bar to the North section of the layout
        add(topPanel, BorderLayout.NORTH);

        // --- Main List Area ---
        // Create the panel that will vertically stack the movies
        moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBackground(BACKGROUND_COLOR);

        // Fetch movies from database and add a visual card for each movie
        for (Movie m : getAllMovies()) {
            moviePanel.add(createMovieCard(m));
        }

        // Wrap the movie panel in a scroll pane for navigation through the list
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Increase scroll speed/smoothness
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); // Remove default border for a cleaner look
        add(scrollPane, BorderLayout.CENTER);

        // Use invokeLater to ensure the scroll bar resets to the top after the UI renders
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }


    // Loads an image from the resources
    private ImageIcon loadResourceImage(String imagePath, int width, int height) {
        // Choose the file name, fallback to default if null
        String fileName = (imagePath == null ? "default.jpg" : imagePath);
        // Try to get the image from classpath
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        // If not found, fallback to default image
        if (resource == null) resource = getClass().getResource("/images/default.jpg");
        // Convert the URL resource to an Image object
        Image img = new ImageIcon(resource).getImage();
        // Scale the image smoothly and return as ImageIcon
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    /**
     * Creates a stylized JPanel (card) representing a single movie.
     * @param movie The movie object containing data.
     * @return A JPanel configured with movie info.
     */
    private JPanel createMovieCard(Movie movie) {
        // Create card with a BorderLayout and spacing
        JPanel card = new JPanel(new BorderLayout(15, 0));
        // Add a bottom border for separation and internal padding
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        card.setBackground(BACKGROUND_COLOR);

        // 1. Movie Poster Section
        ImageIcon icon = loadResourceImage(movie.getImagePath(), 130, 150);
        JLabel posterLabel = new JLabel(icon);
        card.add(posterLabel, BorderLayout.WEST);

        // 2. Info Panel Section
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        // Movie Title styling
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Vertical spacer

        // Movie Description styling (uses JTextArea for multi-line support)
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descriptionArea.setForeground(new Color(60, 60, 60));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(BACKGROUND_COLOR);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(550, 80));
        infoPanel.add(descriptionArea);

        // Metadata Panel (Duration, Rating, Date)
        JPanel infoDetailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        infoDetailsPanel.setBackground(BACKGROUND_COLOR);
        infoDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String movieInfo = "🕒 " + movie.getDuration() + " min  |  ⭐ " + movie.getRating() + "  |  📅 " + movie.getReleaseDate();
        JLabel infoLabel = new JLabel(movieInfo);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        infoLabel.setForeground(new Color(80, 80, 80));
        infoDetailsPanel.add(infoLabel);
        infoPanel.add(infoDetailsPanel);

        card.add(infoPanel, BorderLayout.CENTER);

        // 3. Action Button Section
        JPanel btnContainer = new JPanel(new GridBagLayout()); // Use GridBag to center button vertically
        btnContainer.setBackground(BACKGROUND_COLOR);
        JButton btn = new JButton("Watch Showtimes");
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 150));
        // Action listener to switch to showtimes view
        btn.addActionListener(e -> openShowtimesPage(movie.getId()));
        btnContainer.add(btn);

        card.add(btnContainer, BorderLayout.EAST);
        // Constrain card size for layout consistency
        card.setMaximumSize(new Dimension(900, 700));

        return card;
    }

    /**
     * Connects to the database and retrieves all movie records.
     * @return A list of Movie objects.
     */
    private List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        // Use try-with-resources for automatic closing of DB connections
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM movies");
             ResultSet rs = pst.executeQuery()) {

            // Map each result row to a Movie object
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
        } catch (SQLException ex) {
            // Show error message if database interaction fails
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
        return movies;
    }

    /**
     * Navigates to the WatchShowtimesPage for a specific movie.
     * @param movieId ID of the selected movie.
     */
    private void openShowtimesPage(int movieId) {
        new WatchShowtimesPage(movieId).setVisible(true);
        this.dispose(); // Close the current listing page
    }

    /**
     * Clears user session and returns to the Login Form.
     */
    private void logout() {
        UserSession.logout();
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        new LoginForm().setVisible(true);
        this.dispose(); // Close current window
    }

}
