// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.auth.UserSession;
import cinema.database.DatabaseConnection;
import cinema.models.Movie;
import cinema.models.Showtime;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.sql.*;

/**
 * BookingConfirmationPage handles the final step of the ticket purchase process.
 * It displays movie details, selected seats, and the total price, and handles database insertion.
 */
public class BookingConfirmationPage extends JFrame {
    // Member variables to store booking details passed from previous screens
    private List<String> selectedSeats;     // e.g., ["A1", "A2"]
    private List<Integer> selectedSeatIds;  // Database IDs for the seats
    private double totalPrice;
    private Movie movie;
    private Showtime showtime;
    private int hallId;
    private int screeningId;

    /**
     * Constructor: Initializes data and builds the UI components
     */
    public BookingConfirmationPage(List<String> selectedSeats, List<Integer> selectedSeatIds, double totalPrice, Movie movie, Showtime showtime, int hallId, int screeningId) {
        this.selectedSeats = selectedSeats;
        this.selectedSeatIds = selectedSeatIds;
        this.totalPrice = totalPrice;
        this.movie = movie;
        this.showtime = showtime;
        this.hallId = hallId;
        this.screeningId = screeningId;

        // Window basic configuration
        setTitle("Confirm Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null); // Centers the window on screen
        setLayout(new BorderLayout());

        // --- TOP NAV BAR ---
        // Creates a black header containing a "Back" button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.BLACK);

        JButton backBtn = new JButton("← Back");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.BLACK);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Action: Return to the seat selection screen
        backBtn.addActionListener(e -> goBackToSeats());

        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        // Container for movie details and payment actions
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));
        content.setBackground(Color.WHITE);

        // --- Poster Image Logic ---
        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Determine which file to load
        String fileName = (movie.getImagePath() == null ? "default.jpg" : movie.getImagePath());
        // Try to load image from resources
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        // Fallback to default image if requested one is missing
        if (resource == null) {
            resource = getClass().getResource("/images/default.jpg");
        }
        // Fail fast if even default image is missing
        if (resource == null) {
            throw new RuntimeException("Image not found: " + fileName);
        }
        // Load and scale the image
        Image img = new ImageIcon(resource).getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
        posterLabel.setIcon(new ImageIcon(img));

        // --- Information Labels ---
        // Displaying Title, Show details, and Total Price
        JLabel title = createLabel(movie.getTitle(), new Font("SansSerif", Font.BOLD, 28), Color.BLACK);
        JLabel info = createLabel(showtime.getStartTime() + " | Seats: " + String.join(", ", selectedSeats),
                new Font("SansSerif", Font.PLAIN, 22), Color.DARK_GRAY);
        JLabel price = createLabel("Total: €" + String.format("%.2f", totalPrice),
                new Font("SansSerif", Font.BOLD, 26), new Color(34, 150, 243));

        // --- Confirm Button ---
        // Final action button to trigger the payment/booking logic
        JButton confirmBtn = new JButton("Confirm & Pay Now");
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setBackground(new Color(46, 204, 113)); // Success Green
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());

        // Adding components to the content panel with vertical spacing
        content.add(posterLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(title);
        content.add(info);
        content.add(Box.createVerticalStrut(20));
        content.add(price);
        content.add(Box.createVerticalStrut(25));
        content.add(confirmBtn);

        // Add content to frame inside a scroll pane (in case screen is small)
        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    /**
     * Helper method to reduce boilerplate code for creating styled labels
     */
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(5, 0, 5, 0));
        return l;
    }

    /**
     * core Logic: Validates user session, checks for double-booking, and inserts records into DB
     */
    private void confirmBooking() {
        // 1. Security check: User must be logged in
        if (!UserSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to continue.");
            return;
        }

        // 2. Final confirmation prompt
        int choice = JOptionPane.showConfirmDialog(this, "Confirm payment?", "Payment", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 3. Race Condition Check: Ensure seats weren't booked by another user while this page was open
            String checkSQL = "SELECT COUNT(*) FROM tickets WHERE screening_id = ? AND seat_id = ? AND status = 'BOOKED'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                for (int sid : selectedSeatIds) {
                    checkStmt.setInt(1, showtime.getId());
                    checkStmt.setInt(2, sid);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "One of your seats was just taken!");
                        return;
                    }
                }
            }

            // 4. Database Insertion: Record the booking for each seat
            String insertSQL = "INSERT INTO tickets (user_id, screening_id, seat_id, status) VALUES (?, ?, ?, 'BOOKED')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                for (int sid : selectedSeatIds) {
                    insertStmt.setInt(1, UserSession.getUserId());
                    insertStmt.setInt(2, showtime.getId());
                    insertStmt.setInt(3, sid);
                    insertStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Booking Confirmed! Enjoy your movie!");
                this.dispose(); // Close the confirmation page(this window)
                new MovieListingPage().setVisible(true); // Redirect to movie listing
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Navigation: Closes current page and re-opens the Seat Selection page
     */
    private void goBackToSeats() {
        new SeatSelectionPage(hallId, movie.getId(), screeningId, selectedSeats).setVisible(true);
        this.dispose();
    }
}
