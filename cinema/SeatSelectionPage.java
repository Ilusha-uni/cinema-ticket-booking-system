package cinema;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class SeatSelectionPage extends JFrame {
    private int hallId, movieId, screeningId;
    private JPanel seatPanel;
    private List<JButton> seatButtons = new ArrayList<>();
    private List<Seat> seats;
    private List<String> preSelectedSeats;
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    public SeatSelectionPage(int hallId, int movieId, int screeningId, List<String> preSelectedSeats) {
        this.hallId = hallId;
        this.movieId = movieId;
        this.screeningId = screeningId;
        this.preSelectedSeats = (preSelectedSeats == null) ? new ArrayList<>() : preSelectedSeats;

        setTitle("Seat Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // 1. TOP PANEL: Keeping the Black "Back" Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.BLACK); // Keeps the requested black theme
        JButton backBtn = new JButton("← Back to Showtimes");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.BLACK);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            this.dispose();
            new WatchShowtimesPage(movieId).setVisible(true);
        });
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER PANEL: Simplified Grid for seats
        seatPanel = new JPanel(new GridLayout(0, 10, 5, 5));
        seats = getSeatsForHall(hallId);
        displaySeats();
        add(new JScrollPane(seatPanel), BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Confirmation Button
        JButton confirmBtn = new JButton("Go to Confirmation");
        confirmBtn.setBackground(PRIMARY_BLUE);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setBackground(PRIMARY_BLUE);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        confirmBtn.addActionListener(e -> redirectToConfirmationPage());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirmBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void displaySeats() {
        for (Seat seat : seats) {
            // Simplified seat text
            JButton btn = new JButton("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());
            btn.putClientProperty("seatData", seat);

            // Logic for colors
            if (seat.isBooked()) {
                btn.setBackground(Color.RED);
                btn.setEnabled(false);
            } else if (preSelectedSeats.contains("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber())) {
                btn.setBackground(Color.YELLOW);
            } else {
                btn.setBackground(Color.GREEN);
            }

            btn.addActionListener(e -> {
                if (btn.getBackground() == Color.GREEN) {
                    btn.setBackground(Color.YELLOW);
                } else {
                    btn.setBackground(Color.GREEN);
                }
            });

            seatButtons.add(btn);
            seatPanel.add(btn);
        }
    }

    private void redirectToConfirmationPage() {
        List<String> selectedNames = new ArrayList<>();
        List<Integer> selectedIds = new ArrayList<>();

        for (JButton btn : seatButtons) {
            if (btn.getBackground() == Color.YELLOW) {
                Seat seat = (Seat) btn.getClientProperty("seatData");
                selectedNames.add("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());
                selectedIds.add(seat.getId());
            }
        }

        if (!selectedIds.isEmpty()) {
            double totalPrice = selectedIds.size() * getShowtimeDetails(screeningId).getPrice();
            new BookingConfirmationPage(selectedNames, selectedIds, totalPrice, getMovieDetails(movieId), getShowtimeDetails(screeningId), hallId, screeningId).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.");
        }
    }

    // --- LOGIC METHODS (Unchanged from original) ---

    private List<Seat> getSeatsForHall(int hallId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT s.id, s.row_number, s.seat_number, s.seat_type, " +
                "CASE WHEN EXISTS (SELECT 1 FROM tickets t WHERE t.seat_id = s.id AND t.screening_id = ? AND t.status = 'BOOKED') THEN 'BOOKED' ELSE 'AVAILABLE' END AS seat_status " +
                "FROM seats s WHERE s.hall_id = ? ORDER BY s.row_number, s.seat_number";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, screeningId);
            pst.setInt(2, hallId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(rs.getInt("id"), hallId, rs.getInt("row_number"), rs.getInt("seat_number"), rs.getString("seat_type"), "BOOKED".equals(rs.getString("seat_status"))));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return seats;
    }

    private Movie getMovieDetails(int movieId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM movies WHERE id = ?")) {
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"), rs.getDouble("rating"), rs.getString("release_date"), rs.getString("image_path"));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    private Showtime getShowtimeDetails(int screeningId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM screenings WHERE id = ?")) {
            pst.setInt(1, screeningId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return new Showtime(rs.getInt("id"), rs.getInt("movie_id"), rs.getInt("hall_id"), rs.getString("start_time"), rs.getDouble("price"));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }
}