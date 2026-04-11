package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionPage extends JFrame {
    private int hallId;
    private int movieId;
    private JPanel seatPanel;
    private List<JButton> seatButtons = new ArrayList<>();
    private List<Seat> seats;
    private List<String> preSelectedSeats;
    private int screeningId;

    public SeatSelectionPage(int hallId, int movieId, int screeningId, List<String> preSelectedSeats) {
        this.hallId = hallId;
        this.movieId = movieId;
        this.screeningId = screeningId;
        int userId = UserSession.getUserId();

        // Ensure preSelectedSeats is never null
        if (preSelectedSeats == null) {
            this.preSelectedSeats = new ArrayList<>();
        } else {
            this.preSelectedSeats = preSelectedSeats;
        }

        // Basic setup for the JFrame
        setTitle("Seat Selection");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the "Back to Showtimes" button
        addBackToShowtimesButton();

        // Initialize the seat panel with a grid layout
        seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(0, 10, 10, 10));  // 10 seats per row, adjust as needed
        seatPanel.setBackground(Color.WHITE);

        // Fetch seats for the given hall from the database
        seats = getSeatsForHall(hallId);

        // Display the seat buttons dynamically
        for (Seat seat : seats) {
            JButton seatButton = new JButton("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());
            seatButton.putClientProperty("seatData", seat);  // Attach seat data to the button

            String seatName = seatButton.getText();

            // Highlight pre-selected seats
            if (this.preSelectedSeats.contains(seatName)) {
                seatButton.setBackground(Color.YELLOW);  // Pre-selected seats
            }

            if (seat.isBooked()) {
                seatButton.setBackground(Color.RED);  // Booked seats
                seatButton.setEnabled(false);  // Disable interaction with booked seats
            } else if (this.preSelectedSeats.contains(seatName)) {
                seatButton.setBackground(Color.YELLOW);  // Selected seats
            } else {
                seatButton.setBackground(Color.GREEN);  // Available seats
                seatButton.setEnabled(true);

                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleSeatSelection(seatButton);  // Toggle seat selection when clicked
                    }
                });
            }

            seatButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            seatButton.setFocusPainted(false);
            seatButtons.add(seatButton);
            seatPanel.add(seatButton);  // Add the seat button to the panel
        }

        // Add scrollable panel for seats
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // "Go to Confirmation" Button to confirm seat selection
        JButton goToConfirmationButton = new JButton("Go to Confirmation");
        goToConfirmationButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        goToConfirmationButton.setBackground(new Color(0, 122, 255));  // Blue button color
        goToConfirmationButton.setForeground(Color.WHITE);
        goToConfirmationButton.setFocusPainted(false);
        goToConfirmationButton.setPreferredSize(new Dimension(190, 40));  // Button size
        goToConfirmationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redirectToConfirmationPage();  // Redirect to the booking confirmation page
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(goToConfirmationButton);
        add(buttonPanel, BorderLayout.SOUTH);  // Add button at the bottom

        // Final window settings
        setSize(800, 800);  // Size of the window
        setLocationRelativeTo(null);  // Center window on screen
    }

    // Add "Back to Showtimes" button styled like "Back to Movies"
    private void addBackToShowtimesButton() {
        JButton backBtn = new JButton("← Back to Showtimes");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(18, 18, 18));  // Same color as the navbar
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> backToShowtimes());  // Action listener for the button

        // Create panel to hold the button on the left side
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(18, 18, 18));  // Same color as navbar
        topPanel.setPreferredSize(new Dimension(getWidth(), 45));
        topPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        topPanel.add(backBtn, BorderLayout.WEST);  // Add button to the left

        // Add the topPanel to the frame
        add(topPanel, BorderLayout.NORTH);
    }

    // Handle "Back to Showtimes" action
    private void backToShowtimes() {
        this.dispose();  // Close current page
        new WatchShowtimesPage(movieId).setVisible(true);  // Open the Watch Showtimes page again
    }

    // Toggle seat selection when clicked (green for available, yellow for selected)
    private void toggleSeatSelection(JButton seatButton) {
        if (seatButton.getBackground() == Color.GREEN) {
            seatButton.setBackground(Color.YELLOW);  // Select seat (change to yellow)
        } else if (seatButton.getBackground() == Color.YELLOW) {
            seatButton.setBackground(Color.GREEN);  // Deselect seat (change back to green)
        }
    }

    // Method to fetch seat data from the database
    private List<Seat> getSeatsForHall(int hallId) {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT s.id, s.hall_id, s.row_number, s.seat_number, s.seat_type, " +
                    "CASE WHEN EXISTS (SELECT 1 FROM tickets t WHERE t.seat_id = s.id  AND t.screening_id = ? AND t.status = 'BOOKED') " +
                    "THEN 'BOOKED' ELSE 'AVAILABLE' END AS seat_status " +
                    "FROM seats s " +
                    "WHERE s.hall_id = ? " +
                    "ORDER BY s.row_number ASC, s.seat_number ASC";  // Fetch sorted seats
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, screeningId);
            pst.setInt(2, hallId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int seatId = rs.getInt("id");
                int rowNumber = rs.getInt("row_number");
                int seatNumber = rs.getInt("seat_number");
                String seatType = rs.getString("seat_type");
                String seatStatus = rs.getString("seat_status");
                boolean isBooked = seatStatus.equals("BOOKED");
                seats.add(new Seat(seatId, hallId, rowNumber, seatNumber, seatType, isBooked));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching seat data: " + ex.getMessage());
        }
        return seats;
    }

    // Redirect to confirmation page after selecting seats
    private void redirectToConfirmationPage() {
        // Fetch movie and showtime details
        Movie movie = getMovieDetails(movieId);
        Showtime showtime = getShowtimeDetails(screeningId);

        if (showtime == null) {
            JOptionPane.showMessageDialog(this, "Error: Could not retrieve pricing.");
            return;
        }

        // Prepare selected seat data
        List<String> selectedNames = new ArrayList<>();
        List<Integer> selectedIds = new ArrayList<>();

        for (JButton seatButton : seatButtons) {
            if (seatButton.getBackground() == Color.YELLOW) {
                selectedNames.add(seatButton.getText());  // Add selected seat's name
                Seat s = (Seat) seatButton.getClientProperty("seatData");
                if (s != null) {
                    selectedIds.add(s.getId());  // Add the seat ID
                }
            }
        }

        if (!selectedIds.isEmpty()) {
            double totalPrice = selectedIds.size() * showtime.getPrice();
            new BookingConfirmationPage(selectedNames, selectedIds, totalPrice, movie, showtime, hallId, screeningId).setVisible(true);
            this.dispose();  // Close current page
        } else {
            JOptionPane.showMessageDialog(this, "No seats selected.");
        }
    }

    // Method to fetch movie details
    private Movie getMovieDetails(int movieId) {
        Movie movie = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM movies WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                movie = new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                        rs.getInt("duration_minutes"), rs.getDouble("rating"),
                        rs.getString("release_date"), rs.getString("image_path"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching movie details: " + ex.getMessage());
        }
        return movie;
    }

    // Method to fetch showtime details
    private Showtime getShowtimeDetails(int screeningId) {
        Showtime showtime = null;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = "SELECT * FROM screenings WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, screeningId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                showtime = new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getInt("hall_id"),
                        rs.getString("start_time"),
                        rs.getDouble("price")
                );
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

                    int screeningId = rs.getInt("id"); // or screening_id depending on your table
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
