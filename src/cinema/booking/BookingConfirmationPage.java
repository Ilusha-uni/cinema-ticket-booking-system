// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.auth.UserSession;
import cinema.models.Movie;
import cinema.models.Showtime;
import cinema.dao.BookingDAO;

// Import necessary libraries for GUI (Swing/AWT)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


/**
 * BookingConfirmationPage represents the final step in the cinema ticket booking process.
 *
 * Responsibilities:
 * 1. Displays selected movie details: poster, title, showtime, hall, and selected seats.
 * 2. Shows the total price for the selected seats.
 * 3. Allows the user to confirm the booking and triggers database insertion.
 * 4. Performs validation:
 *    - Ensures the user is logged in.
 *    - Checks that seats are still available (prevents double-booking).
 * 5. Handles navigation:
 *    - Back to seat selection if the user wants to change seats.
 *    - Redirects to movie listing after successful booking.
 */
public class BookingConfirmationPage extends JFrame {
    /** List of seat names selected by the user. */
    private List<String> selectedSeats;

    /** Database IDs for the selected seats. */
    private List<Integer> selectedSeatIds;

    /** Total price for the booking. */
    private double totalPrice;

    /** Movie being booked. */
    private Movie movie;

    /** Showtime of the selected movie. */
    private Showtime showtime;

    /** Hall ID for seat selection. */
    private int hallId;

    /** Screening ID for the showtime. */
    private int screeningId;

    /**
     * Constructs the booking confirmation page with all relevant booking details.
     *
     * @param selectedSeats Names of seats selected
     * @param selectedSeatIds Database IDs of seats
     * @param totalPrice Total price for selected seats
     * @param movie Movie being booked
     * @param showtime Selected showtime
     * @param hallId Hall ID
     * @param screeningId Screening ID
     */
    public BookingConfirmationPage(List<String> selectedSeats, List<Integer> selectedSeatIds, double totalPrice, Movie movie, Showtime showtime, int hallId, int screeningId) {
        this.selectedSeats = selectedSeats;
        this.selectedSeatIds = selectedSeatIds;
        this.totalPrice = totalPrice;
        this.movie = movie;
        this.showtime = showtime;
        this.hallId = hallId;
        this.screeningId = screeningId;
        setupWindow();
        setupTopBar();
        setupMainContent();
    }

    /**
     * Configures the basic JFrame properties: title, size, close operation, and layout.
     */
    private void setupWindow() {
        // Window basic configuration
        setTitle("Confirm Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /** Sets up the top navigation bar with a back button. */
    private void setupTopBar() {
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
    }

    /** Sets up the main content area showing movie info, seats, price, and confirm button. */
    private void setupMainContent() {
        // Container for movie details and payment actions
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));
        content.setBackground(Color.WHITE);

        // --- Poster Image Logic ---
        JLabel posterLabel = loadPosterImage();

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
     * Loads and scales the movie poster image.
     *
     * @return JLabel containing the movie poster
     */
    private JLabel loadPosterImage () {
        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Determine which file to load
        String fileName = (movie.getImagePath() == null ? "default.jpg" : movie.getImagePath());
        // Try to load image from resources
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        // Fallback to default image if requested one is missing
        if (resource == null) resource = getClass().getResource("/images/default.jpg");
        // Fail fast if even default image is missing
        if (resource == null) throw new RuntimeException("Image not found: " + fileName);

        // Load and scale the image
        Image img = new ImageIcon(resource).getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
        posterLabel.setIcon(new ImageIcon(img));

        return posterLabel;
    }

    /**
     * Creates a JLabel with specified text, font, color, and center alignment.
     */
    private JLabel createLabel (String text, Font font, Color color){
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(5, 0, 5, 0));
        return l;
    }

    /**
     * Validates user session, checks for double-booking, and inserts records into DB
     */
    private void confirmBooking() {
        // Ensure the user is logged in before allowing any booking.
        if (!UserSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to continue.");
            return; // Exit method if user is not authenticated
        }

        // Ask the user to confirm payment; returns YES or NO
        int choice = JOptionPane.showConfirmDialog(this, "Confirm payment?", "Payment", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return; // User declined, cancel booking

        // BookingDAO handles all database interactions related to ticket bookings
        BookingDAO bookingDAO = new BookingDAO();

        // Prevents race conditions where another user might have booked a seat
        if (bookingDAO.areSeatsBooked(showtime.getId(), selectedSeatIds)) {
            JOptionPane.showMessageDialog(this, "One of your seats was just taken!");
            return;
        }
        // Inserts booking records into the database for all selected seats
        boolean booked = bookingDAO.bookSeats(UserSession.getUserId(), showtime.getId(), selectedSeatIds);
        if (booked) {
            // Booking successful
            JOptionPane.showMessageDialog(this, "Booking Confirmed! Enjoy your movie!");
            this.dispose(); // Close the confirmation page(this window)
            new MovieListingPage().setVisible(true); // Redirect to movie listing
        } else {
            // Booking failed
            JOptionPane.showMessageDialog(this, "Booking failed. Please try again.", "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Navigation: Closes current page and re-opens the Seat Selection page
     */
    private void goBackToSeats () {
        new SeatSelectionPage(hallId, movie.getId(), screeningId, selectedSeats).setVisible(true);
        this.dispose();
    }
}
