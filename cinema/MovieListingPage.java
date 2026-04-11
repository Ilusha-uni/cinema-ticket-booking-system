package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieListingPage extends JFrame {

    private JPanel moviePanel;

    private static final Color NAV_BAR_COLOR = new Color(18, 18, 18);
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);

    public MovieListingPage() {
        setTitle("Movie Listings");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        moviePanel = new JPanel(new GridLayout(0, 1, 10, 10));
        moviePanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(moviePanel);
        add(scrollPane, BorderLayout.CENTER);

        List<Movie> movies = getAllMovies();
        for (Movie movie : movies) {
            moviePanel.add(createMovieCard(movie));
        }

        addLogoutButton();

        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    // ================= DATABASE =================
    private List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM movies");
             ResultSet rs = pst.executeQuery()) {

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
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        return movies;
    }

    // ================= MOVIE CARD =================
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        card.add(createPoster(movie), BorderLayout.WEST);
        card.add(createDetails(movie), BorderLayout.CENTER);
        card.add(createButtonPanel(movie), BorderLayout.EAST);

        return card;
    }

    // ================= POSTER =================
    private JLabel createPoster(Movie movie) {
        String path = "images/" + (movie.getImagePath() == null ? "default.jpg" : movie.getImagePath());
        File file = new File(path);
        String finalPath = file.exists() ? path : "images/default.jpg";

        Image img = new ImageIcon(finalPath).getImage();
        ImageIcon icon = new ImageIcon(img.getScaledInstance(150, 225, Image.SCALE_SMOOTH));

        return new JLabel(icon);
    }

    // ================= DETAILS =================
    private JPanel createDetails(Movie movie) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panel.setOpaque(false);

        panel.add(createTitle(movie));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(createDescription(movie));
        panel.add(createMetaData(movie));

        return panel;
    }

    private JLabel createTitle(Movie movie) {
        JLabel label = new JLabel(movie.getTitle());
        label.setFont(TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JScrollPane createDescription(Movie movie) {
        JTextArea area = new JTextArea(movie.getDescription());
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(BODY_FONT);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 60));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(null);

        return scroll;
    }

    private JPanel createMetaData(Movie movie) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(new JLabel("🕒 " + movie.getDuration() + " min"));
        panel.add(new JLabel("⭐ " + movie.getRating()));
        panel.add(new JLabel("📅 " + movie.getReleaseDate()));

        return panel;
    }

    // ================= BUTTON =================
    private JPanel createButtonPanel(Movie movie) {
        JButton button = new JButton("Watch Showtimes");
        button.setPreferredSize(new Dimension(180, 200));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.addActionListener(e -> openShowtimesPage(movie.getId()));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(button);

        return wrapper;
    }

    // ================= NAVIGATION =================
    private void openShowtimesPage(int movieId) {
        new WatchShowtimesPage(movieId).setVisible(true);
        this.dispose();
    }

    private void addLogoutButton() {
        JButton logoutButton = new JButton("← Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(NAV_BAR_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> logout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 45));
        topPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        topPanel.add(logoutButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void logout() {
        UserSession.logout();
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        new LoginForm().setVisible(true);
        this.dispose();
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieListingPage().setVisible(true);
        });
    }
}