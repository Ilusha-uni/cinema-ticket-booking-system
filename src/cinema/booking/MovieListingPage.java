// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.auth.LoginForm;
import cinema.auth.UserSession;
import cinema.models.Movie;
import cinema.dao.MovieDAO;

// Import necessary libraries for GUI (Swing/AWT) and utility classes
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * MovieListingPage Class
 *
 * Represents the main dashboard where users can view available movies.
 * Responsibilities:
 * - Display a list of movies retrieved from the database.
 * - Provide a top navigation bar with a logout button.
 * - Allow navigation to the showtimes page for each movie.
 */
public class MovieListingPage extends JFrame {

    // Panel that holds the list of movie cards
    private JPanel moviePanel;

    // Constant colors for a consistent UI theme
    private static final Color NAV_BAR_COLOR = new Color(18, 18, 18); // Dark top navigation bar
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243); // Primary button color
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); // Background color for main content

    /**
     * Constructor for MovieListingPage.
     * Initializes the JFrame window and all UI components.
     */
    public MovieListingPage() {
        setupWindow();          // Configure basic window properties
        setupNavigationBar();   // Add top navigation bar with logout
        setupMainContent();     // Add movie list panel with scroll
    }

    /**
     * Configures the basic JFrame properties: title, size, close operation, and background.
     */
    private void setupWindow() {
        setTitle("Movie Listings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);                // Initial window dimensions
        setLocationRelativeTo(null);       // Center window on the screen
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    /**
     * Sets up the top navigation bar containing a logout button.
     * Adds the panel to the JFrame.
     */
    private void setupNavigationBar() {
        // Create top panel with FlowLayout for left-aligned logout button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        // Create and style logout button
        JButton logoutBtn = new JButton("← Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(NAV_BAR_COLOR);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout()); // Handle logout action

        // Add button to top panel and attach panel to JFrame
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Sets up the main content area where movie cards will be displayed.
     * Uses a vertical BoxLayout inside a scroll pane.
     */
    private void setupMainContent() {
        // Create vertical stacking panel for movie cards
        moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBackground(BACKGROUND_COLOR);

        // Populate panel with movie cards from the database
        populateMovies();

        // Wrap the movie panel in a scroll pane for vertical navigation
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); // Clean look without borders
        add(scrollPane, BorderLayout.CENTER);

        // Ensure scroll bar starts at the top after UI renders
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    /**
     * Retrieves all movies from the database and adds a visual card for each to the moviePanel.
     */
    private void populateMovies() {
        MovieDAO movieDAO = new MovieDAO();
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            moviePanel.add(createMovieCard(m));
        }
    }

    /**
     * Loads and scales an image from the resources folder.
     *
     * @param imagePath Relative path to the image (inside /images/)
     * @param width Desired width for display
     * @param height Desired height for display
     * @return Scaled ImageIcon ready to be used in JLabels
     */
    private ImageIcon loadResourceImage(String imagePath, int width, int height) {
        String fileName = (imagePath == null ? "default.jpg" : imagePath);
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        if (resource == null) resource = getClass().getResource("/images/default.jpg"); // fallback
        Image img = new ImageIcon(resource).getImage();
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    /**
     * Creates a JPanel card representing a single movie.
     * The card includes poster, title, description, metadata, and a button to view showtimes.
     *
     * @param movie The Movie object containing all necessary data
     * @return JPanel representing a movie card
     */
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(15, 0)); // Horizontal spacing between sections
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), // Bottom separator
                new EmptyBorder(15, 15, 15, 15) // Internal padding
        ));
        card.setBackground(BACKGROUND_COLOR);

        // 1. Movie Poster Section (WEST)
        ImageIcon icon = loadResourceImage(movie.getImagePath(), 130, 150);
        JLabel posterLabel = new JLabel(icon);
        card.add(posterLabel, BorderLayout.WEST);

        // 2. Movie Info Section (CENTER)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        // Movie Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer

        // Movie Description (multi-line)
        JTextArea descriptionArea = getJTextArea(movie);
        infoPanel.add(descriptionArea);

        // Metadata: Duration, Rating, Release Date
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

        // 3. Action Button Section (EAST)
        JPanel btnContainer = new JPanel(new GridBagLayout()); // Center vertically
        btnContainer.setBackground(BACKGROUND_COLOR);
        JButton btn = new JButton("Watch Showtimes");
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 150));
        btn.addActionListener(e -> openShowtimesPage(movie.getId())); // Navigate to showtimes page
        btnContainer.add(btn);
        card.add(btnContainer, BorderLayout.EAST);

        // Constrain card size for layout consistency
        card.setMaximumSize(new Dimension(900, 700));
        return card;
    }

    /**
     * Helper method to create a multi-line JTextArea for movie descriptions.
     *
     * @param movie Movie object
     * @return Configured JTextArea
     */
    private JTextArea getJTextArea(Movie movie) {
        JTextArea descriptionArea = new JTextArea(movie.getDescription());
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descriptionArea.setForeground(new Color(60, 60, 60));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(BACKGROUND_COLOR);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(550, 80));
        return descriptionArea;
    }

    /**
     * Opens the WatchShowtimesPage for the given movie.
     *
     * @param movieId ID of the selected movie
     */
    private void openShowtimesPage(int movieId) {
        new WatchShowtimesPage(movieId).setVisible(true);
        this.dispose(); // Close current window to avoid duplicates
    }

    /**
     * Logs out the current user and returns to the login form.
     */
    private void logout() {
        UserSession.logout(); // Clear user session
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        new LoginForm().setVisible(true); // Show login page
        this.dispose(); // Close current window
    }
}
