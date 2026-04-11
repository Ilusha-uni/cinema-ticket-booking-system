package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.sql.*;

public class BookingConfirmationPage extends JFrame {
    private List<String> selectedSeats;
    private List<Integer> selectedSeatIds;
    private double totalPrice;
    private Movie movie;
    private Showtime showtime;
    private int hallId;
    private int screeningId;

    public BookingConfirmationPage(List<String> selectedSeats, List<Integer> selectedSeatIds, double totalPrice, Movie movie, Showtime showtime, int hallId, int screeningId) {
        this.selectedSeats = selectedSeats;
        this.selectedSeatIds = selectedSeatIds;
        this.totalPrice = totalPrice;
        this.movie = movie;
        this.showtime = showtime;
        this.hallId = hallId;
        this.screeningId = screeningId;

        setTitle("Booking Confirmation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Movie Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        contentPanel.add(createMoviePanel(), gbc);

        // 2. Showtime Details
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.5;
        contentPanel.add(createShowtimePanel(), gbc);

        // 3. Selected Seats List
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(createSeatsPanel(), gbc);

        // 4. Confirm Button
        JButton confirmBtn = new JButton("Confirm & Pay €" + String.format("%.2f", totalPrice));
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 18));
        confirmBtn.setBackground(new Color(34, 150, 243));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        contentPanel.add(confirmBtn, gbc);

        // 5. Back Button
        JButton backBtn = new JButton("Back to Seats");
        backBtn.setBackground(new Color(200, 0, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> goBackToSeats());
        gbc.gridy = 3;
        contentPanel.add(backBtn, gbc);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        setSize(850, 650);
        setLocationRelativeTo(null);
    }

    private void confirmBooking() {
        int userId = UserSession.getUserId();
        System.out.println("Logged-in User ID: " + userId); // Debugging the logged-in user ID
        if (!UserSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "You must be logged in to book tickets.");
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Process payment for " + selectedSeats.size() + " seats?", "Final Step", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) return;



        try (Connection conn = DatabaseConnection.getConnection()) {
            // STEP 1: Check if any seat was booked while the user was on this page
            String checkSQL = "SELECT COUNT(*) FROM tickets WHERE screening_id = ? AND seat_id = ? AND status = 'BOOKED'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                for (int seatId : selectedSeatIds) {
                    checkStmt.setInt(1, showtime.getId());
                    checkStmt.setInt(2, seatId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Sorry, one of your selected seats has just been taken. Please choose another one.");
                        return;
                    }
                }
            }

            // STEP 2: Perform the booking (INSERT)
            String insertSQL = "INSERT INTO tickets (user_id, screening_id, seat_id, status) VALUES (?, ?, ?, 'BOOKED')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                for (int seatId : selectedSeatIds) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, showtime.getId());
                    insertStmt.setInt(3, seatId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                JOptionPane.showMessageDialog(this, "Booking Successful!");
                this.dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void goBackToSeats() {
        new SeatSelectionPage(hallId, movie.getId(), screeningId, selectedSeats).setVisible(true);
        this.dispose();
    }

    private JPanel createMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Color.WHITE);
        JLabel posterLabel = new JLabel(loadImage(movie.getImagePath()));
        panel.add(posterLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        textPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel(movie.getTitle());
        title.setFont(new Font("Serif", Font.BOLD, 28));
        JTextArea desc = new JTextArea(movie.getDescription());
        desc.setLineWrap(true); desc.setWrapStyleWord(true); desc.setEditable(false);
        textPanel.add(title);
        textPanel.add(new JScrollPane(desc));
        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private ImageIcon loadImage(String path) {
        String fullPath = "images/" + (path == null || path.isEmpty() ? "default.jpg" : path);
        ImageIcon icon = new ImageIcon(fullPath);
        Image img = icon.getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Show Details"));
        panel.add(new JLabel(" Time: " + showtime.getStartTime()));
        panel.add(new JLabel(" Unit Price: €" + showtime.getPrice()));
        panel.add(new JLabel(" Duration: " + movie.getDuration() + " mins"));
        return panel;
    }

    private JPanel createSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Your Selection"));
        JList<String> seatList = new JList<>(selectedSeats.toArray(new String[0]));
        panel.add(new JScrollPane(seatList), BorderLayout.CENTER);
        JLabel totalLabel = new JLabel("TOTAL: €" + String.format("%.2f", totalPrice), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(totalLabel, BorderLayout.SOUTH);
        return panel;
    }



private void logout() {
        UserSession.logout();
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        new LoginForm().setVisible(true);  // Redirect back to login screen
        this.dispose(); // Close the current window
    }
}