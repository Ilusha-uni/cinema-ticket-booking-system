package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.*;


public class BookingConfirmationPage extends JFrame {
    private List<String> selectedSeats;
    private double totalPrice;
    private Movie movie;
    private Showtime showtime;

    public BookingConfirmationPage(List<String> selectedSeats, double totalPrice, Movie movie, Showtime showtime) {
        this.selectedSeats = selectedSeats;
        this.totalPrice = totalPrice;
        this.movie = movie;
        this.showtime = showtime;

        setTitle("Booking Confirmation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Better for multi-window apps
        setLayout(new BorderLayout());

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Movie Section (Top)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        contentPanel.add(createMoviePanel(), gbc);

        // 2. Showtime Details (Bottom Left)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        contentPanel.add(createShowtimePanel(), gbc);

        // 3. Selected Seats (Bottom Right)
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(createSeatsPanel(), gbc);

        // 4. Confirm Button (Bottom Span)
        JButton confirmBtn = new JButton("Confirm & Pay €" + String.format("%.2f", totalPrice));
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 18));
        confirmBtn.setBackground(new Color(34, 150, 243));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setOpaque(true);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        contentPanel.add(confirmBtn, gbc);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        setSize(850, 650);
        setLocationRelativeTo(null);
    }

    private JPanel createMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Color.WHITE);

        // Scaled Image
        JLabel posterLabel = new JLabel(loadImage(movie.getImagePath()));
        panel.add(posterLabel, BorderLayout.WEST);

        // Text Info
        JPanel textPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        textPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("Serif", Font.BOLD, 28));

        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBackground(Color.WHITE);
        desc.setFont(new Font("Arial", Font.ITALIC, 14));

        textPanel.add(title);
        textPanel.add(new JScrollPane(desc));
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private ImageIcon loadImage(String path) {
        try {
            // Standardizing path to look in images folder
            String fullPath = "images/" + (path == null || path.isEmpty() ? "default.jpg" : path);
            ImageIcon icon = new ImageIcon(fullPath);
            Image img = icon.getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon(); // Return blank icon on failure
        }
    }

    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Show Details",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        panel.add(new JLabel(" 🕒 Time: " + showtime.getStartTime()));
        panel.add(new JLabel(" 💵 Unit Price: €" + showtime.getPrice()));
        panel.add(new JLabel(" 🎬 Duration: " + movie.getDuration() + " mins"));

        return panel;
    }

    private JPanel createSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Your Selection",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        JList<String> seatList = new JList<>(selectedSeats.toArray(new String[0]));
        seatList.setVisibleRowCount(4);
        panel.add(new JScrollPane(seatList), BorderLayout.CENTER);

        JLabel totalLabel = new JLabel("TOTAL: €" + String.format("%.2f", totalPrice), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(200, 0, 0));
        panel.add(totalLabel, BorderLayout.SOUTH);

        return panel;
    }

    private boolean isSeatBooked(int seatId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND seat_id = ? AND status = 'BOOKED'";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, seatId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If there's any booking for this user, return true
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;  // Return false if no booking exists for this user
    }
    private void confirmBooking() {
        int response = JOptionPane.showConfirmDialog(this,
                "Process payment for " + selectedSeats.size() + " seats?",
                "Final Step", JOptionPane.YES_NO_OPTION);

        // Check if the user is logged in
        if (!UserSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "You must be logged in to book tickets.");
            return;
        }

        // Retrieve the logged-in user's ID
        int userId = UserSession.getUserId();

        // First, check if the user exists in the users table
        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkUserQuery = "SELECT id FROM users WHERE id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();

                // If the user doesn't exist, show an error message
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "User not found. Please log in again.");
                    return;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking user existence: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        if (response == JOptionPane.YES_OPTION) {
            // Perform the database operations for booking tickets
            try {
                // Insert tickets into the database
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String insertSQL = "INSERT INTO tickets (user_id, screening_id, seat_id, booked_at, status) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                        for (String seat : selectedSeats) {
                            int seatId = getSeatId(seat); // Get the seat ID for the current seat

                            // Check if the seat is already booked for the user
                            if (isSeatBooked(seatId, userId)) {
                                JOptionPane.showMessageDialog(this, "Seat " + seat + " is already booked.");
                                continue; // Skip this seat if it's already booked
                            }

                            // Proceed with inserting the ticket as it's not booked yet
                            stmt.setInt(1, userId);
                            stmt.setInt(2, showtime.getId());
                            stmt.setInt(3, seatId);
                            stmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                            stmt.setString(5, "BOOKED");
                            stmt.addBatch();
                        }

                        stmt.executeBatch(); // Execute all inserts

                        // Optionally, update seat status to "BOOKED"
                        updateSeatStatus("BOOKED");

                        JOptionPane.showMessageDialog(this, "Success! Your tickets are ready.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while processing your booking. Please try again.");
            }

            // Close the booking window
            this.dispose();
        }
    }

    private int getSeatId(String seat) {
        // Implement the logic to retrieve the seat_id from the seat name (e.g., "A1", "B2")
        return Integer.parseInt(seat.replaceAll("[^0-9]", ""));
    }

    private int getUserId() {
        // Retrieve the logged-in user ID. For now, just returning a placeholder value.
        return 1; // Replace this with actual logic to get user ID
    }

    private void updateSeatStatus(String status) {
        // Convert seat names to seat IDs for the query
        List<Integer> seatIds = new ArrayList<>();
        for (String seat : selectedSeats) {
            int seatId = getSeatId(seat); // This method converts seat names (e.g., "A1") to seat IDs
            seatIds.add(seatId);
        }

        // Generate the placeholders for the IN clause (e.g., ?, ?, ?)
        String placeholders = String.join(",", Collections.nCopies(seatIds.size(), "?"));

        // Create the SQL query with placeholders for seat IDs
        String updateSQL = "UPDATE tickets SET status = ? WHERE seat_id IN (" + placeholders + ") AND status = 'AVAILABLE'";

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
                // Set the status first (e.g., 'BOOKED')
                stmt.setString(1, status);

                // Set the seat IDs
                for (int i = 0; i < seatIds.size(); i++) {
                    stmt.setInt(i + 2, seatIds.get(i));  // Seat IDs start from position 2
                }

                // Execute the update
                int updatedRows = stmt.executeUpdate();

                if (updatedRows == 0) {
                    JOptionPane.showMessageDialog(this, "One or more seats are already booked.");
                } else {
                    JOptionPane.showMessageDialog(this, "Seats successfully booked.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating seat status in the tickets table.");
        }
    }

    private void logout() {
        UserSession.logout();
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        new LoginForm().setVisible(true);  // Redirect back to login screen
        this.dispose(); // Close the current window
    }
}