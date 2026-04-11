

package cinema;

import javax.swing.*;
        import javax.swing.border.EmptyBorder;
import java.awt.*;
        import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class MovieListingPage extends JFrame {

    private JPanel moviePanel;
    private static final Color NAV_BAR_COLOR = new Color(18, 18, 18);
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);
    // Updated to a Light Grey background
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);

    public MovieListingPage() {
        setTitle("Movie Listings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);

        // --- Top Black Bar ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        JButton logoutBtn = new JButton("← Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(NAV_BAR_COLOR);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- Main List Area ---
        moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.Y_AXIS));
        moviePanel.setBackground(BACKGROUND_COLOR);

        for (Movie m : getAllMovies()) {
            moviePanel.add(createMovieCard(m));
        }

        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // FIX: Ensure scroll starts at the top
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        card.setBackground(BACKGROUND_COLOR);

        // 1. Poster
        String path = "images/" + (movie.getImagePath() == null ? "default.jpg" : movie.getImagePath());
        ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(110, 150, Image.SCALE_SMOOTH));
        JLabel posterLabel = new JLabel(icon);
        card.add(posterLabel, BorderLayout.WEST);

        // 2. Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        // Title (Dark text for light background)
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Description
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

        // Movie Info (Left Aligned FlowLayout)
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

        // 3. Action Button
        JPanel btnContainer = new JPanel(new GridBagLayout()); // Centers button vertically
        btnContainer.setBackground(BACKGROUND_COLOR);
        JButton btn = new JButton("Watch Showtimes");
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 150));
        btn.addActionListener(e -> openShowtimesPage(movie.getId()));
        btnContainer.add(btn);

        card.add(btnContainer, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(900, 700));

        return card;
    }

    private List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM movies");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                movies.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                        rs.getInt("duration_minutes"), rs.getDouble("rating"),
                        rs.getString("release_date"), rs.getString("image_path")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
        return movies;
    }

    private void openShowtimesPage(int movieId) {
        new WatchShowtimesPage(movieId).setVisible(true);
        this.dispose();
    }

    private void logout() {
        UserSession.logout();
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        new LoginForm().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovieListingPage().setVisible(true));
    }
}