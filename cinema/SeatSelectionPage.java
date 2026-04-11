package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionPage extends JFrame {
    private int hallId, movieId, screeningId;
    private JPanel seatPanel;
    private List<JButton> seatButtons = new ArrayList<>();
    private List<Seat> seats;
    private List<String> preSelectedSeats;

    public SeatSelectionPage(int hallId, int movieId, int screeningId, List<String> preSelectedSeats) {
        this.hallId = hallId;
        this.movieId = movieId;
        this.screeningId = screeningId;
        this.preSelectedSeats = (preSelectedSeats == null) ? new ArrayList<>() : preSelectedSeats;

        setTitle("Seat Selection");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add "Back to Showtimes" button
        addBackToShowtimesButton();

        // Initialize the seat panel with a grid layout
        seatPanel = new JPanel(new GridLayout(0, 10, 10, 10));  // 10 seats per row, adjust as needed
        seatPanel.setBackground(Color.WHITE);

        // Fetch and display seats dynamically
        seats = getSeatsForHall(hallId);
        displaySeats();

        // Add scrollable panel for seats
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // "Go to Confirmation" Button
        addConfirmationButton();

        setSize(800, 800);  // Size of the window
        setLocationRelativeTo(null);  // Center window on screen
    }

    private void addBackToShowtimesButton() {
        JButton backBtn = new JButton("← Back to Showtimes");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(18, 18, 18));  // Same color as the navbar
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> backToShowtimes());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(18, 18, 18));  // Same color as navbar
        topPanel.setPreferredSize(new Dimension(getWidth(), 45));
        topPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        topPanel.add(backBtn, BorderLayout.WEST);  // Add button to the left

        add(topPanel, BorderLayout.NORTH);
    }

    private void backToShowtimes() {
        this.dispose();
        new WatchShowtimesPage(movieId).setVisible(true);
    }

    private void displaySeats() {
        for (Seat seat : seats) {
            JButton seatButton = createSeatButton(seat);
            seatPanel.add(seatButton);
            seatButtons.add(seatButton);
        }
    }

    private JButton createSeatButton(Seat seat) {
        JButton seatButton = new JButton("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());
        seatButton.putClientProperty("seatData", seat);  // Attach seat data to the button
        updateSeatButtonState(seatButton, seat);
        seatButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        seatButton.setFocusPainted(false);
        seatButton.addActionListener(e -> toggleSeatSelection(seatButton));
        return seatButton;
    }

    private void updateSeatButtonState(JButton seatButton, Seat seat) {
        String seatName = seatButton.getText();

        // Set initial state
        if (seat.isBooked()) {
            seatButton.setBackground(Color.RED);  // Booked seats
            seatButton.setEnabled(false);  // Disable interaction with booked seats
        } else if (preSelectedSeats.contains(seatName)) {
            seatButton.setBackground(Color.YELLOW);  // Selected seats
        } else {
            seatButton.setBackground(Color.GREEN);  // Available seats
            seatButton.setEnabled(true);
        }
    }

    private void toggleSeatSelection(JButton seatButton) {
        if (seatButton.getBackground() == Color.GREEN) {
            seatButton.setBackground(Color.YELLOW);  // Select seat
        } else {
            seatButton.setBackground(Color.GREEN);  // Deselect seat
        }
    }

    private void addConfirmationButton() {
        JButton goToConfirmationButton = new JButton("Go to Confirmation");
        goToConfirmationButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        goToConfirmationButton.setBackground(new Color(0, 122, 255));  // Blue button color
        goToConfirmationButton.setForeground(Color.WHITE);
        goToConfirmationButton.setFocusPainted(false);
        goToConfirmationButton.setPreferredSize(new Dimension(190, 40));  // Button size
        goToConfirmationButton.addActionListener(e -> redirectToConfirmationPage());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(goToConfirmationButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void redirectToConfirmationPage() {
        List<String> selectedNames = new ArrayList<>();
        List<Integer> selectedIds = new ArrayList<>();

        for (JButton seatButton : seatButtons) {
            if (seatButton.getBackground() == Color.YELLOW) {
                selectedNames.add(seatButton.getText());
                Seat seat = (Seat) seatButton.getClientProperty("seatData");
                if (seat != null) {
                    selectedIds.add(seat.getId());
                }
            }
        }

        if (!selectedIds.isEmpty()) {
            double totalPrice = selectedIds.size() * getShowtimeDetails(screeningId).getPrice();
            new BookingConfirmationPage(selectedNames, selectedIds, totalPrice, getMovieDetails(movieId), getShowtimeDetails(screeningId), hallId, screeningId).setVisible(true);
            this.dispose();  // Close current page
        } else {
            JOptionPane.showMessageDialog(this, "No seats selected.");
        }
    }

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
                int seatId = rs.getInt("id");
                int rowNumber = rs.getInt("row_number");
                int seatNumber = rs.getInt("seat_number");
                String seatType = rs.getString("seat_type");
                boolean isBooked = "BOOKED".equals(rs.getString("seat_status"));
                seats.add(new Seat(seatId, hallId, rowNumber, seatNumber, seatType, isBooked));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching seat data: " + ex.getMessage());
        }
        return seats;
    }

    private Movie getMovieDetails(int movieId) {
        Movie movie = null;
        String query = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                movie = new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"),
                        rs.getDouble("rating"), rs.getString("release_date"), rs.getString("image_path"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching movie details: " + ex.getMessage());
        }
        return movie;
    }

    private Showtime getShowtimeDetails(int screeningId) {
        Showtime showtime = null;
        String query = "SELECT * FROM screenings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, screeningId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                showtime = new Showtime(rs.getInt("id"), rs.getInt("movie_id"), rs.getInt("hall_id"), rs.getString("start_time"), rs.getDouble("price"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching showtime details: " + ex.getMessage());
        }
        return showtime;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT id, hall_id, movie_id FROM screenings LIMIT 1";
                PreparedStatement pst = conn.prepareStatement(query);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    int hallId = rs.getInt("hall_id");
                    int movieId = rs.getInt("movie_id");
                    int screeningId = rs.getInt("id");
                    new SeatSelectionPage(hallId, movieId, screeningId, new ArrayList<>()).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "No showtimes available.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });
    }
}