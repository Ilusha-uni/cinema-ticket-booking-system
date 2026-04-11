package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchShowtimesPage extends JFrame {

    private JPanel showtimeContainer;
    private final int movieId;

    // Modern Color Palette
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color NAV_BAR_COLOR = new Color(18, 18, 18);
    private final Color ACCENT_BLUE = new Color(0, 122, 255);
    private final Color TEXT_DARK = new Color(33, 33, 33);
    private final Color TEXT_GRAY = new Color(110, 110, 110);

    public WatchShowtimesPage(int movieId) {
        this.movieId = movieId;
        setupFrame();

        // Navigation Bar
        add(createNavbar(), BorderLayout.NORTH);

        // Main Content (Scrollable)
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(BACKGROUND_COLOR);

        Movie movie = getMovieDetails(movieId);
        if (movie != null) {
            contentWrapper.add(createEnhancedMovieHeader(movie));
        }

        // --- SECTION TITLE ---
        JLabel sectionTitle = new JLabel("Available Showtimes");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sectionTitle.setForeground(TEXT_DARK);

        // Key Fix: Set alignment and wrap in a FlowLayout(Left) panel to prevent centering
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(30, 35, 10, 40));
        titlePanel.add(sectionTitle);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentWrapper.add(titlePanel);

        // --- SHOWTIMES CONTAINER ---
        showtimeContainer = new JPanel();
        showtimeContainer.setLayout(new BoxLayout(showtimeContainer, BoxLayout.Y_AXIS));
        showtimeContainer.setBackground(BACKGROUND_COLOR);
        showtimeContainer.setBorder(new EmptyBorder(10, 40, 40, 40));
        showtimeContainer.setAlignmentX(Component.LEFT_ALIGNMENT); // Keep container to the left

        List<Showtime> showtimes = getShowtimesForMovie(movieId);
        if (showtimes.isEmpty()) {
            JLabel emptyLabel = new JLabel("No showtimes available for this movie yet.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            showtimeContainer.add(emptyLabel);
        } else {
            for (Showtime s : showtimes) {
                showtimeContainer.add(createModernShowtimeCard(s));
                showtimeContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        contentWrapper.add(showtimeContainer);

        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Showtimes - " + movieId);
        setSize(1100, 805);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(NAV_BAR_COLOR);
        nav.setPreferredSize(new Dimension(getWidth(), 45));
        nav.setBorder(new EmptyBorder(0, 20, 0, 20));

        JButton backBtn = new JButton("← Back to Movies");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(NAV_BAR_COLOR);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.addActionListener(e -> backToMovies());

        nav.add(backBtn, BorderLayout.WEST);
        return nav;
    }

    private JPanel createEnhancedMovieHeader(Movie movie) {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 30, 40));
        header.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure header stays left

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST; // Anchor components to the West
        gbc.fill = GridBagConstraints.NONE;

        // Poster Image
        JLabel posterLabel = new JLabel(loadImage(movie.getImagePath()));
        posterLabel.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        header.add(posterLabel, gbc);

        // Movie Details Panel
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));  // Vertical stacking of components
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(0, 30, 0, 0));  // Ensure no padding is causing clipping

// Movie Title
        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));  // Ensure consistent font size
        title.setAlignmentX(Component.LEFT_ALIGNMENT);  // Align title to the left

// Set the preferred size of the title if needed
        title.setPreferredSize(new Dimension(600, 50));  // Adjust the height for enough space

// Add title to the details panel
        details.add(title);
        details.add(Box.createRigidArea(new Dimension(0, 8)));  // Space between title and meta



        // Replace normal hyphens with non-breaking hyphens
        String releaseDate = movie.getReleaseDate().replace("-", "\u2011"); // Unicode non-breaking hyphen

// Build meta string with non-breaking spaces around emojis and separators
        String metaText = String.format("<html>⭐ %.1f  •  🕒 %d min  •  📅 %s  </html>",
                movie.getRating(),
                movie.getDuration(),
                releaseDate);

// Replace normal spaces with non-breaking spaces to prevent line breaks
        metaText = metaText.replace(" ", "\u00A0"); // Unicode non-breaking space

        JLabel meta = new JLabel(metaText);
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        meta.setForeground(TEXT_GRAY);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);




        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setForeground(new Color(80, 80, 80));
        desc.setMaximumSize(new Dimension(600, 100));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(title);
        details.add(Box.createRigidArea(new Dimension(0, 8)));
        details.add(meta);
        details.add(Box.createRigidArea(new Dimension(0, 15)));
        details.add(desc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0;
        header.add(details, gbc);

        return header;
    }

    private JPanel createModernShowtimeCard(Showtime showtime) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Important: Use a larger MaximumSize or don't set it to ensure it stretches
        card.setMaximumSize(new Dimension(1000, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JPanel timeInfo = new JPanel(new GridLayout(2, 1));
        timeInfo.setOpaque(false);
        JLabel time = new JLabel(showtime.getStartTime());
        time.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel hall = new JLabel("Premium Hall " + showtime.getHallId());
        hall.setForeground(TEXT_GRAY);
        timeInfo.add(time);
        timeInfo.add(hall);

        JLabel price = new JLabel("€" + String.format("%.2f", showtime.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 22));
        price.setForeground(new Color(46, 125, 50));
        price.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btn = new JButton("Select Seats");
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setBackground(ACCENT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> selectSeat(showtime));

        card.add(timeInfo, BorderLayout.WEST);
        card.add(price, BorderLayout.CENTER);
        card.add(btn, BorderLayout.EAST);

        return card;
    }

    // --- Database & Logic Methods (Existing) ---
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
        } catch (SQLException ex) { ex.printStackTrace(); }
        return showtimes;
    }

    private ImageIcon loadImage(String imagePath) {
        File imageFile = new File("images/" + (imagePath == null ? "default.jpg" : imagePath));
        String path = imageFile.exists() ? imageFile.getAbsolutePath() : "images/default.jpg";
        Image img = new ImageIcon(path).getImage();
        return new ImageIcon(img.getScaledInstance(120, 180, Image.SCALE_SMOOTH));
    }

    private void selectSeat(Showtime showtime) {
        new SeatSelectionPage(showtime.getHallId(), showtime.getMovieId(), showtime.getId(), new ArrayList<String>()).setVisible(true);
        this.dispose();
    }

    private void backToMovies() {
        new MovieListingPage().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WatchShowtimesPage(1).setVisible(true));
    }
}