// Define the package name for the cinema application
package cinema;

// Import necessary Java Swing and AWT classes for the GUI
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WatchShowtimesPage class represents a window that displays available
 * screening times for a specific movie selected by the user.
 */
public class WatchShowtimesPage extends JFrame {

    // Unique identifier for the selected movie
    private final int movieId;

    // UI Constants for consistent styling (Colors)
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color NAV_BAR_COLOR = new Color(18, 18, 18);
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    /**
     * Constructor for the Showtimes Page.
     * Initializes the frame and builds the user interface.
     */
    public WatchShowtimesPage(int movieId) {
        this.movieId = movieId;

        // Window basic configuration
        setTitle("Movie Showtimes");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Black Top Navigation Bar ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        // Create and configure the back button
        JButton backBtn = new JButton("← Back to Movies");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(NAV_BAR_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> backToMovies());
        topPanel.add(backBtn);

        // Add navbar to the top of the frame
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Content Container ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Fetch movie details from database and add header if data exists
        Movie movie = getMovieDetails(movieId);
        if (movie != null) {
            mainContent.add(createSimpleHeader(movie));
            mainContent.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical spacing
        }

        // --- 3. Showtimes List ---
        // Fetch list of screenings for this movie
        List<Showtime> showtimes = getShowtimesForMovie(movieId);
        if (showtimes.isEmpty()) {
            // Display message if no screenings are scheduled
            mainContent.add(new JLabel("No showtimes available."));
        } else {
            // Loop through each showtime and add a row to the UI
            for (Showtime s : showtimes) {
                mainContent.add(createSimpleShowtimeRow(s));
                mainContent.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing between rows
            }
        }

        // Add the content inside a scroll pane for long lists
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    /**
     * Creates a header panel displaying the movie poster, title, and metadata.
     */
    private JPanel createSimpleHeader(Movie movie) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false); // Make transparent to show background
        header.setMaximumSize(new Dimension(1000, 200));

        // Load and display movie poster
        JLabel poster = new JLabel(loadImage(movie.getImagePath()));
        header.add(poster, BorderLayout.WEST);

        // Text information panel
        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);

        // Title styling
        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Metadata: Rating, Duration, and Release Date
        JLabel meta = new JLabel(String.format("⭐ %.1f | %d min | %s",
                movie.getRating(), movie.getDuration(), movie.getReleaseDate()));
        meta.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Description area with word wrapping
        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setFont(new Font("SansSerif", Font.PLAIN, 16));
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);

        // Assemble the info panel
        info.add(title);
        info.add(meta);
        info.add(desc);

        header.add(info, BorderLayout.CENTER);
        return header;
    }

    /**
     * Creates a row for a single showtime including time, price, and selection button.
     */
    private JPanel createSimpleShowtimeRow(Showtime s) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 15, 10, 15)));
        row.setMaximumSize(new Dimension(1000, 70));

        // Showtime and Hall ID text
        JLabel time = new JLabel(s.getStartTime() + " - Hall " + s.getHallId());
        time.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Price display formatted to 2 decimal places
        JLabel price = new JLabel("€" + String.format("%.2f", s.getPrice()));
        price.setFont(new Font("SansSerif", Font.BOLD, 20));
        price.setForeground(new Color(0, 153, 51)); // Green color for price

        // Selection button
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
     * Retrieves Movie data from the database using the movie ID.
     */
    private Movie getMovieDetails(int movieId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM movies WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                        rs.getInt("duration_minutes"), rs.getDouble("rating"),
                        rs.getString("release_date"), rs.getString("image_path"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    /**
     * Retrieves all showtimes (screenings) for a specific movie ID, ordered by time.
     */
    private List<Showtime> getShowtimesForMovie(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM screenings WHERE movie_id = ? ORDER BY start_time ASC";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                showtimes.add(new Showtime(rs.getInt("id"), rs.getInt("movie_id"),
                        rs.getInt("hall_id"), rs.getString("start_time"), rs.getDouble("price")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return showtimes;
    }

    /**
     * Safely loads an image file and scales it for the UI.
     */
    private ImageIcon loadImage(String imagePath) {
        File imageFile = new File("images/" + (imagePath == null ? "default.jpg" : imagePath));
        // Fallback to default image if file does not exist
        String path = imageFile.exists() ? imageFile.getAbsolutePath() : "images/default.jpg";
        Image img = new ImageIcon(path).getImage();
        // Return a smooth-scaled version of the image
        return new ImageIcon(img.getScaledInstance(130, 180, Image.SCALE_SMOOTH));
    }

    /**
     * Closes current page and opens the seat selection screen.
     */
    private void selectSeat(Showtime showtime) {
        new SeatSelectionPage(showtime.getHallId(), showtime.getMovieId(), showtime.getId(), new ArrayList<String>()).setVisible(true);
        this.dispose();
    }

    /**
     * Closes current page and returns to the movie list screen.
     */
    private void backToMovies() {
        new MovieListingPage().setVisible(true);
        this.dispose();
    }
}
