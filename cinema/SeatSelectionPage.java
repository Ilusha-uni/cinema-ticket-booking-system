package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;  // Explicitly import java.util.List


public class SeatSelectionPage extends JFrame {
    private int hallId;
    private int movieId; // Store movieId
    private JPanel seatPanel;
    private List<JButton> seatButtons = new ArrayList<>();  // Use List instead of Map
    private List<Seat> seats;

    public SeatSelectionPage(int hallId, int movieId) {
        this.hallId = hallId;
        this.movieId = movieId;

        setTitle("Seat Selection");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the seat panel with GridLayout for seats
        seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(10, 10, 10, 10)); // Adjust the size accordingly, here 10x10 grid
        seatPanel.setBackground(Color.WHITE);

        // Fetch seat data for the selected hall and display it
        // Fetch seat data for the selected hall and display it
        seats = getSeatsForHall(hallId);

// Get the userId (for logged-in user)
        int userId = UserSession.getUserId(); // Assuming UserSession stores the logged-in user info

// Display seats in the panel with their unique row and seat number
        for (Seat seat : seats) {
            JButton seatButton = new JButton("Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber());

            // Check if the seat is already booked by the current user
            if (isSeatBooked(seat.getId(), userId)) {
                seatButton.setBackground(Color.RED);  // Booked seats are red
                seatButton.setEnabled(false);  // Disable button for booked seats
            } else {
                seatButton.setBackground(Color.GREEN);  // Available seats are green
                seatButton.setEnabled(true);  // Enable the button for available seats

                // Add event listener for selecting/deselecting seats
                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleSeatSelection(seatButton);
                    }
                });
            }

            seatButton.setFont(new Font("Arial", Font.PLAIN, 12));
            seatButton.setFocusPainted(false);
            seatButtons.add(seatButton);
            seatPanel.add(seatButton);
        }


        // Scrollable area for the seat panel (if there are too many seats)
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        add(scrollPane, BorderLayout.CENTER);


        // Other code for buttons (Go to Confirmation and Go Back)...

        // "Go to Confirmation" Button to redirect to BookingConfirmationPage
        JButton goToConfirmationButton = new JButton("Go to Confirmation");
        goToConfirmationButton.setFont(new Font("Arial", Font.BOLD, 16));
        goToConfirmationButton.setBackground(new Color(34, 150, 243));  // Blue button color
        goToConfirmationButton.setForeground(Color.WHITE);
        goToConfirmationButton.setFocusPainted(false);
        goToConfirmationButton.setPreferredSize(new Dimension(190, 40));  // Button size
        goToConfirmationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redirectToConfirmationPage();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(goToConfirmationButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // "Go Back" Button to go back to the previous page
        JButton backButton = new JButton("Back to Screenings");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(200, 0, 0));  // Red button color for "Go Back"
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(190, 40));  // Button size
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();  // Pass the movieId back to the previous page
            }
        });

        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        add(backButtonPanel, BorderLayout.NORTH);  // Add the back button to the top of the window

        // Window settings
        setSize(800, 800);
        setLocationRelativeTo(null);  // Center the window
    }


    private boolean isSeatBooked(int seatId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND seat_id = ? AND status = 'BOOKED'";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);  // Make sure user_id is passed correctly
            pst.setInt(2, seatId);   // Ensure seat_id is mapped correctly to the database seat
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If there's any booking for this user, return true
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;  // Return false if no booking exists for this user
    }

    // Fetch seat data from the database for the given hall
    private List<Seat> getSeatsForHall(int hallId) {
        List<Seat> seats = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT seats.id, seats.hall_id, seats.row_number, seats.seat_number, seats.seat_type, " +
                    "IFNULL(tickets.status, 'AVAILABLE') AS seat_status " +
                    "FROM seats LEFT JOIN tickets ON seats.id = tickets.seat_id " +
                    "WHERE seats.hall_id = ?";  // Fetching seats for the hall with seat status
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, hallId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int seatId = rs.getInt("id");
                int rowNumber = rs.getInt("row_number");
                int seatNumber = rs.getInt("seat_number");
                String seatType = rs.getString("seat_type");
                String seatStatus = rs.getString("seat_status");

                // Determine if the seat is booked or available
                boolean isBooked = seatStatus.equals("BOOKED");

                // Add seat to the list
                seats.add(new Seat(seatId, hallId, rowNumber, seatNumber, seatType, isBooked));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching seat data: " + ex.getMessage());
        }

        return seats;
    }

    // Toggle seat selection when clicked
    private void toggleSeatSelection(JButton seatButton) {
        if (seatButton.getBackground() == Color.GREEN) {
            // Seat selected
            seatButton.setBackground(Color.YELLOW); // Selected seats will be yellow
        } else if (seatButton.getBackground() == Color.YELLOW) {
            // Deselect seat
            seatButton.setBackground(Color.GREEN); // Available seats are green
        }
    }

    // Redirect to the Booking Confirmation page
    private void redirectToConfirmationPage() {
        // 1. Fetch details first so we have the correct price from the DB
        Movie movie = getMovieDetails(movieId);
        Showtime showtime = getShowtimeDetails(hallId, movieId);

        if (showtime == null) {
            JOptionPane.showMessageDialog(this, "Error: Could not retrieve pricing for this showtime.");
            return;
        }

        // 2. Collect selected seats and calculate price using DB value
        List<String> selectedSeats = new ArrayList<>();
        double totalPrice = 0;
        double pricePerSeat = showtime.getPrice(); // Dynamic price from database

        for (JButton seatButton : seatButtons) {
            if (seatButton.getBackground() == Color.YELLOW) {
                selectedSeats.add(seatButton.getText());
                totalPrice += pricePerSeat; // Use the DB price here
            }
        }

        // 3. Proceed to confirmation
        if (!selectedSeats.isEmpty()) {
            new BookingConfirmationPage(selectedSeats, totalPrice, movie, showtime).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No seats selected.");
        }
    }

    // Method to fetch movie details
    private Movie getMovieDetails(int movieId) {
        Movie movie = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM movies WHERE id = ?";  // Fetching movie details
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, movieId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int duration = rs.getInt("duration_minutes");
                double rating = rs.getDouble("rating");
                String releaseDate = rs.getString("release_date");
                String imagePath = rs.getString("image_path");  // Fetch image path

                // Pass imagePath as the 7th argument to the Movie constructor
                movie = new Movie(id, title, description, duration, rating, releaseDate, imagePath);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching movie details: " + ex.getMessage());
        }

        return movie;
    }

    // Method to fetch showtime details
    private Showtime getShowtimeDetails(int hallId, int movieId) {
        Showtime showtime = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM screenings WHERE hall_id = ? AND movie_id = ?";  // Fetching showtime details
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, hallId);   // Pass hallId
            pst.setInt(2, movieId);  // Pass movieId
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String startTime = rs.getString("start_time");
                double price = rs.getDouble("price");

                // Pass hallId, movieId, and other details to Showtime constructor
                showtime = new Showtime(hallId, movieId, hallId, startTime, price);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching showtime details: " + ex.getMessage());
        }

        return showtime;
    }

    // Go back to the previous page (Pass the movieId)
    private void goBack() {
        this.dispose();  // Close the current page
        new WatchShowtimesPage(movieId).setVisible(true);  // Pass the actual movieId
    }

    public static void main(String[] args) {
        // Display the seat selection page for a specific hall (for example, Hall ID 3)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SeatSelectionPage(3, 1).setVisible(true); // Pass movieId when calling
            }
        });
    }
}