// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.dao.MovieDAO;
import cinema.dao.ShowtimeDAO;
import cinema.models.Movie;
import cinema.models.Showtime;

// Import necessary libraries for GUI (Swing/AWT)
// and utility classes for data collection
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a window that displays available screening times for a specific movie selected by the user.
 *
 * Responsibilities:
 * 1. Fetch the selected movie's details from the database (MovieDAO).
 * 2. Fetch all scheduled showtimes for that movie (ShowtimeDAO).
 * 3. Build a vertical layout with movie header and showtime rows.
 * 4. Provide navigation back to the movie listing page.
 * 5. Allow the user to select a showtime and proceed to seat selection.
 */
public class WatchShowtimesPage extends JFrame {

    /**
     * Unique identifier of the selected movie.
     */
    private final int movieId;

    /**
     * Background color used in the UI.
     */
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);

    /**
     * Color used for the top navigation bar.
     */
    private final Color NAV_BAR_COLOR = new Color(18, 18, 18);

    /**
     * Primary action color used for buttons and highlights.
     */
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    /**
     * Constructor for the WatchShowtimesPage.
     * Initializes the frame and builds the UI components.
     *
     * @param movieId The ID of the movie for which to show showtimes.
     */
    public WatchShowtimesPage(int movieId) {
        this.movieId = movieId;
        setupWindow();          // Configure the main JFrame
        setupNavigationBar();   // Add the top navbar with Back button
        setupMainContent();     // Build movie header and showtime list
    }

    /**
     * Configures the main JFrame window.
     */
    private void setupWindow() {
        setTitle("Movie Showtimes");
        setSize(1300, 800);                // Fixed window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);       // Center on screen
        setLayout(new BorderLayout());     // Main layout for header + content
    }

    /**
     * Sets up the top navigation bar.
     * Contains a back button that returns to the movie listing page.
     */
    private void setupNavigationBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        JButton backBtn = new JButton("← Back to Movies");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(NAV_BAR_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Clicking navigates back to the main movie listing page
        backBtn.addActionListener(e -> backToMovies());

        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH); // Add navbar at top
    }

    /**
     * Builds the main content area.
     * Fetches movie and showtime data from DAOs and creates UI components.
     */
    private void setupMainContent() {
        // Main content panel: vertical layout, padding, background color
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. Movie Header Section ---
        // DAO (Data Access Object) fetches movie from database
        MovieDAO movieDAO = new MovieDAO();
        Movie movie = movieDAO.getMovieById(movieId);

        if (movie != null) {
            // If the movie exists, add a header panel with poster, title, metadata, description
            mainContent.add(createSimpleHeader(movie));
            mainContent.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical spacing
        }

        // --- 2. Showtimes List Section ---
        // DAO fetches all scheduled showtimes for this movie
        ShowtimeDAO showtimeDAO = new ShowtimeDAO();
        List<Showtime> showtimes = showtimeDAO.getShowtimesByMovieId(movieId);

        if (showtimes.isEmpty()) {
            // No showtimes scheduled: display informative message
            mainContent.add(new JLabel("No showtimes available."));
        } else {
            // Add a row for each showtime: time, hall, price, selection button
            for (Showtime s : showtimes) {
                mainContent.add(createSimpleShowtimeRow(s));
                mainContent.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing between rows
            }
        }

        // Wrap main content in a scroll pane for long lists of showtimes
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    /**
     * Creates a header panel displaying movie poster, title, metadata, and description.
     *
     * @param movie The movie object containing all necessary data.
     * @return A JPanel configured as a header for the movie.
     */
    private JPanel createSimpleHeader(Movie movie) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1000, 200));

        // Movie poster on the left
        JLabel poster = new JLabel(loadImage(movie.getImagePath()));
        header.add(poster, BorderLayout.WEST);

        // Info panel: title, metadata, description
        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);

        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        String movieInfo = "🕒 " + movie.getDuration() + " min  |  ⭐ "
                + movie.getRating() + "  |  📅 " + movie.getReleaseDate();
        JLabel infoLabel = new JLabel(movieInfo);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setFont(new Font("SansSerif", Font.PLAIN, 16));
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);

        // Add components to info panel
        info.add(title);
        info.add(infoLabel);
        info.add(desc);

        header.add(info, BorderLayout.CENTER);
        return header;
    }

    /**
     * Creates a row for a single showtime.
     * Includes showtime start, hall number, price, and a "Select Seats" button.
     *
     * @param s The showtime object.
     * @return JPanel representing the showtime row.
     */
    private JPanel createSimpleShowtimeRow(Showtime s) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 15, 10, 15)));
        row.setMaximumSize(new Dimension(1000, 70));

        JLabel time = new JLabel(s.getStartTime() + " - Hall " + s.getHallId());
        time.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel price = new JLabel("€" + String.format("%.2f", s.getPrice()));
        price.setFont(new Font("SansSerif", Font.BOLD, 20));
        price.setForeground(new Color(0, 153, 51)); // Green color

        JButton btn = new JButton("Select Seats");
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Navigate to seat selection on click
        btn.addActionListener(e -> selectSeat(s));

        // Layout components within the row
        row.add(time, BorderLayout.WEST);
        row.add(price, BorderLayout.CENTER);
        row.add(btn, BorderLayout.EAST);

        return row;
    }

    /**
     * Loads and scales a movie poster image from resources.
     * Fallbacks to default image if missing.
     *
     * @param imagePath Relative path to the image file.
     * @return Scaled ImageIcon to display in the UI.
     */
    private ImageIcon loadImage(String imagePath) {
        String fileName = (imagePath == null ? "default.jpg" : imagePath);
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        if (resource == null) {
            resource = getClass().getResource("/images/default.jpg");
        }
        if (resource == null) {
            throw new RuntimeException("Image not found: " + fileName);
        }
        Image img = new ImageIcon(resource).getImage();
        return new ImageIcon(img.getScaledInstance(130, 180, Image.SCALE_SMOOTH));
    }

    /**
     * Opens the seat selection page for the given showtime.
     *
     * @param showtime The selected showtime.
     */
    private void selectSeat(Showtime showtime) {
        new SeatSelectionPage(
                showtime.getHallId(),
                showtime.getMovieId(),
                showtime.getId(),
                new ArrayList<String>()
        ).setVisible(true);
        this.dispose();
    }

    /**
     * Navigates back to the movie listing page.
     */
    private void backToMovies() {
        new MovieListingPage().setVisible(true);
        this.dispose();
    }
}
