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

    private final int movieId;
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color NAV_BAR_COLOR = new Color(18, 18, 18);
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    public WatchShowtimesPage(int movieId) {
        this.movieId = movieId;
        setTitle("Movie Showtimes");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Black Top Navbar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(NAV_BAR_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), 35));

        JButton backBtn = new JButton("← Back to Movies");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(NAV_BAR_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> backToMovies());
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // 2. Content Container
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        Movie movie = getMovieDetails(movieId);
        if (movie != null) {
            mainContent.add(createSimpleHeader(movie));
            mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // 3. Showtimes List
        List<Showtime> showtimes = getShowtimesForMovie(movieId);
        if (showtimes.isEmpty()) {
            mainContent.add(new JLabel("No showtimes available."));
        } else {
            for (Showtime s : showtimes) {
                mainContent.add(createSimpleShowtimeRow(s));
                mainContent.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    private JPanel createSimpleHeader(Movie movie) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1000, 200));

        // Poster
        JLabel poster = new JLabel(loadImage(movie.getImagePath()));
        header.add(poster, BorderLayout.WEST);

        // Info
        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);

        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel meta = new JLabel(String.format("⭐ %.1f | %d min | %s",
                movie.getRating(), movie.getDuration(), movie.getReleaseDate()));
        meta.setFont(new Font("SansSerif", Font.BOLD, 18));

        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setFont(new Font("SansSerif", Font.PLAIN, 16));
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);

        info.add(title);
        info.add(meta);
        info.add(desc);

        header.add(info, BorderLayout.CENTER);
        return header;
    }

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
        price.setForeground(new Color(0, 153, 51));

        JButton btn = new JButton("Select Seats");
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));

        btn.addActionListener(e -> selectSeat(s));

        row.add(time, BorderLayout.WEST);
        row.add(price, BorderLayout.CENTER);
        row.add(btn, BorderLayout.EAST);

        return row;
    }

    // --- LOGIC (UNTOUCHED) ---
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
        return new ImageIcon(img.getScaledInstance(130, 180, Image.SCALE_SMOOTH));
    }

    private void selectSeat(Showtime showtime) {
        new SeatSelectionPage(showtime.getHallId(), showtime.getMovieId(), showtime.getId(), new ArrayList<String>()).setVisible(true);
        this.dispose();
    }

    private void backToMovies() {
        new MovieListingPage().setVisible(true);
        this.dispose();
    }
}