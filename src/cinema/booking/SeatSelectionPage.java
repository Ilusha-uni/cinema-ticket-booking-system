// Package declaration
package cinema.booking;

// Import project-specific classes
import cinema.models.Movie;
import cinema.models.Seat;
import cinema.models.Showtime;
import cinema.dao.SeatDAO;
import cinema.dao.MovieDAO;
import cinema.dao.ShowtimeDAO;

// Import necessary libraries for GUI (Swing/AWT)
// and utility classes for data collection
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SeatSelectionPage represents the UI for users to view and select seats for a specific movie screening.
 * - Displays a grid of seats for the selected hall and screening.
 * - Allows users to select/deselect seats.
 * - Navigates to BookingConfirmationPage with the selected seats.
 */
public class SeatSelectionPage extends JFrame {
    /** ID of the hall where the screening is taking place */
    private int hallId;

    /** ID of the movie being booked */
    private int movieId;

    /** ID of the specific screening */
    private int screeningId;

    /** Panel containing the grid of seat buttons */
    private JPanel seatPanel;

    /** List of JButton objects representing the seats */
    private List<JButton> seatButtons = new ArrayList<>();

    /** List of Seat objects fetched from the database */
    private List<Seat> seats;

    /** List of previously selected seat names (optional) */
    private List<String> preSelectedSeats;

    // Define a custom blue color used for the primary confirmation button
    private static final Color PRIMARY_BLUE = new Color(34, 150, 243);

    /**
     * Constructor initializes the frame, sets up top navigation, seat grid, and bottom actions.
     *
     * @param hallId ID of the hall
     * @param movieId ID of the movie
     * @param screeningId ID of the specific screening
     * @param preSelectedSeats List of previously selected seats (optional)
     */
    public SeatSelectionPage(int hallId, int movieId, int screeningId, List<String> preSelectedSeats) {
        // Initialize instance variables with values passed from the previous page
        this.hallId = hallId;
        this.movieId = movieId;
        this.screeningId = screeningId;
        // Ensure preSelectedSeats is never null to avoid NullPointerExceptions
        this.preSelectedSeats = (preSelectedSeats == null) ? new ArrayList<>() : preSelectedSeats;
        setupWindow();
        setupTopPanel();
        setupCenterPanel();
        setupBottomPanel();
    }

    /**
     * Configures the main JFrame window: title, size, layout, and screen position.
     */
    private void setupWindow() {
        // Configure the main window (JFrame) properties
        setTitle("Seat Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Use BorderLayout to organize top, center, and bottom sections
        setSize(1300, 800);
        setLocationRelativeTo(null); // Center the window on the screen
    }

    /**
     * Sets up the top navigation panel containing the "Back to Showtimes" button.
     * Clicking the back button disposes the current frame and opens the showtimes page.
     */
    private void setupTopPanel() {
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
            new WatchShowtimesPage(movieId).setVisible(true); // Open show times page
        });

        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Sets up the center panel containing the grid of seats.
     * - Fetches seats from the database using SeatDAO.
     * - Populates the seat panel with buttons representing each seat.
     */
    private void setupCenterPanel() {
        // --- 2. CENTER PANEL: Seat Grid ---
        // Create a grid layout for seats (dynamic rows, 10 columns)
        seatPanel = new JPanel(new GridLayout(0, 10, 5, 5));

        // Create a SeatDAO instance to interact with the database for seat-related queries
        SeatDAO seatDAO = new SeatDAO();
        // Retrieve all seats for the specified hall and screening
        seats = seatDAO.getSeatsForHall(hallId, screeningId);
        displaySeats(); // Method to turn seat data into clickable buttons

        // Add the seat panel inside a scroll pane in case there are many rows
        add(new JScrollPane(seatPanel), BorderLayout.CENTER);
    }

    /**
     * Sets up the bottom panel containing the "Go to Confirmation" button.
     * Clicking this button collects selected seats and redirects to BookingConfirmationPage.
     */
    private void setupBottomPanel() {
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
     * Converts the fetched Seat objects into clickable buttons.
     * - Booked seats are red and disabled.
     * - Previously selected seats are yellow.
     * - Available seats are green.
     * - Clicking toggles between available (green) and selected (yellow).
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
     * Collects all selected seats and redirects to the BookingConfirmationPage.
     * - If no seats are selected, displays a warning message.
     * - Calculates total price based on seat count and showtime price.
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

            // Create DAO instances for Movie and Showtime entities
            MovieDAO movieDAO = new MovieDAO();
            ShowtimeDAO showtimeDAO = new ShowtimeDAO();

            // Retrieve movie details by its ID
            // - movieId: the ID of the selected movie
            // Returns a Movie object containing information like title, duration, and poster
            Movie movie = movieDAO.getMovieById(movieId);
            // Retrieve showtime details by its ID
            // - screeningId: the ID of the selected screening
            // Returns a Showtime object containing information like start time, price, and hall
            Showtime showtime = showtimeDAO.getShowtimeById(screeningId);

            double totalPrice = selectedIds.size() * showtime.getPrice();

            // Open the confirmation page and pass all necessary booking data
            new BookingConfirmationPage(selectedNames, selectedIds, totalPrice,
                    movie, showtime, hallId, screeningId).setVisible(true);
            this.dispose();
        } else {
            // Warn the user if they clicked confirm without selecting anything
            JOptionPane.showMessageDialog(this, "Please select at least one seat.");
        }
    }
}
