// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.models.Movie;
import cinema.models.Seat;
import cinema.models.Showtime;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
// and utility classes for data collection
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 * The SeatSelectionPage class represents a window where users can view and select seats
 * for a specific movie screening in a specific hall.
 */
public class SeatSelectionPage extends JFrame {
    // Fields to store the IDs for the hall, movie, and specific screening
    private int hallId, movieId, screeningId;

    // Panel to hold the grid of seat buttons
    private JPanel seatPanel;

    // Lists to track the button objects, seat data objects, and any previously selected seats
    private List<JButton> seatButtons = new ArrayList<>();
    private List<Seat> seats;
    private List<String> preSelectedSeats;

    // Define a custom blue color used for the primary confirmation button
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    /**
     * Constructor: Initializes the frame, sets up the layout, and populates the seat grid.
     */
    public SeatSelectionPage(int hallId, int movieId, int screeningId, List<String> preSelectedSeats) {
        // Initialize instance variables with values passed from the previous page
        this.hallId = hallId;
        this.movieId = movieId;
        this.screeningId = screeningId;
        // Ensure preSelectedSeats is never null to avoid NullPointerExceptions
        this.preSelectedSeats = (preSelectedSeats == null) ? new ArrayList<>() : preSelectedSeats;

        // Configure the main window (JFrame) properties
        setTitle("Seat Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Use BorderLayout to organize top, center, and bottom sections
        setSize(1300, 800);
        setLocationRelativeTo(null); // Center the window on the screen

        // --- 1. TOP PANEL: Navigation ---
        // Create a black header panel containing a "Back" button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.BLACK);

        JButton backBtn = new JButton("← Back to Showtimes");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.BLACK);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover

        // Return to the previous page when clicked
        backBtn.addActionListener(e -> {
            this.dispose(); // Close current window
            new WatchShowtimesPage(movieId).setVisible(true); // Open showtimes page
        });

        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL: Seat Grid ---
        // Create a grid layout for seats (dynamic rows, 10 columns)
        seatPanel = new JPanel(new GridLayout(0, 10, 5, 5));

        // Fetch seat data from the database for this specific hall and screening
        seats = getSeatsForHall(hallId);
        displaySeats(); // Method to turn seat data into clickable buttons

        // Add the seat panel inside a scroll pane in case there are many rows
        add(new JScrollPane(seatPanel), BorderLayout.CENTER);

        // --- 3. BOTTOM PANEL: Actions ---
        // Create the "Go to Confirmation" button to finalize selection
        JButton confirmBtn = new JButton("Go to Confirmation");
        confirmBtn.setBackground(PRIMARY_BLUE);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Logic to move to the next page
        confirmBtn.addActionListener(e -> redirectToConfirmationPage());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirmBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loops through the list of Seat objects and creates a JButton for each.
     */
    private void displaySeats() {
        for (Seat seat : seats) {
            // Label button with seat coordinates
            JButton btn = new JButton("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());

            // Store the raw Seat object inside the button for easy retrieval later
            btn.putClientProperty("seatData", seat);

            // Determine initial color based on status
            if (seat.isBooked()) {
                btn.setBackground(Color.RED); // Occupied
                btn.setEnabled(false);        // Cannot click booked seats
            } else if (preSelectedSeats.contains("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber())) {
                btn.setBackground(Color.YELLOW); // Already selected in a previous step
            } else {
                btn.setBackground(Color.GREEN); // Available
            }

            // Toggle selection color when clicked (Green <-> Yellow)
            btn.addActionListener(e -> {
                if (btn.getBackground() == Color.GREEN) {
                    btn.setBackground(Color.YELLOW);
                } else {
                    btn.setBackground(Color.GREEN);
                }
            });

            seatButtons.add(btn); // Add to list for tracking
            seatPanel.add(btn);   // Add to the visual panel
        }
    }

    /**
     * Collects IDs of all selected (yellow) seats and navigates to the confirmation screen.
     */
    private void redirectToConfirmationPage() {
        List<String> selectedNames = new ArrayList<>();
        List<Integer> selectedIds = new ArrayList<>();

        // Identify which buttons the user highlighted in yellow
        for (JButton btn : seatButtons) {
            if (btn.getBackground() == Color.YELLOW) {
                Seat seat = (Seat) btn.getClientProperty("seatData");
                selectedNames.add("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());
                selectedIds.add(seat.getId());
            }
        }

        // Proceed if at least one seat is chosen
        if (!selectedIds.isEmpty()) {

            // Final Seat confirmation prompt
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You have selected " + selectedIds.size() + " seat(s).\nDo you want to proceed to checkout?",
                    "Confirm Selection",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            // If the user clicks "No" or closes the dialog, stop the redirection
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            double totalPrice = selectedIds.size() * getShowtimeDetails(screeningId).getPrice();

            // Open the confirmation page and pass all necessary booking data
            new BookingConfirmationPage(selectedNames, selectedIds, totalPrice,
                    getMovieDetails(movieId), getShowtimeDetails(screeningId),
                    hallId, screeningId).setVisible(true);
            this.dispose();
        } else {
            // Warn the user if they clicked confirm without selecting anything
            JOptionPane.showMessageDialog(this, "Please select at least one seat.");
        }
    }

    // --- DATABASE LOGIC METHODS ---

    /**
     * Queries the database for all seats in a hall and checks if they are booked for the given screening.
     */
    private List<Seat> getSeatsForHall(int hallId) {
        List<Seat> seats = new ArrayList<>();
        // SQL query uses a LEFT JOIN or EXISTS check to see if a ticket exists for each seat at this screening time
        String query = "SELECT s.id, s.row_number, s.seat_number, s.seat_type, " +
                "CASE WHEN EXISTS (SELECT 1 FROM tickets t WHERE t.seat_id = s.id AND t.screening_id = ? AND t.status = 'BOOKED') THEN 'BOOKED' ELSE 'AVAILABLE' END AS seat_status " +
                "FROM seats s WHERE s.hall_id = ? ORDER BY s.row_number, s.seat_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, screeningId);
            pst.setInt(2, hallId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Map database rows to Seat objects
                seats.add(new Seat(
                        rs.getInt("id"),
                        hallId,
                        rs.getInt("row_number"),
                        rs.getInt("seat_number"),
                        rs.getString("seat_type"),
                        "BOOKED".equals(rs.getString("seat_status"))
                ));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return seats;
    }

    /**
     * Fetches metadata for a specific movie (Title, Description, etc.) by ID.
     */
    private Movie getMovieDetails(int movieId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM movies WHERE id = ?")) {
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
     * Fetches details for a specific screening (Time, Price, etc.) by ID.
     */
    private Showtime getShowtimeDetails(int screeningId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM screenings WHERE id = ?")) {
            pst.setInt(1, screeningId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Showtime(rs.getInt("id"), rs.getInt("movie_id"), rs.getInt("hall_id"),
                        rs.getString("start_time"), rs.getDouble("price"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }
}
