package cinema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
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

        setTitle("Confirm Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP NAV BAR ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.BLACK);

        JButton backBtn = new JButton("← Back");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.BLACK);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackToSeats());

        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 30, 20, 30));
        content.setBackground(Color.WHITE);

        // Poster Image
        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            String path = "images/" + (movie.getImagePath() == null ? "default.jpg" : movie.getImagePath());
            if (new File(path).exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH));
                posterLabel.setIcon(icon);
            } else {
                posterLabel.setText("[Poster Not Found]");
            }
        } catch (Exception e) { posterLabel.setText("[Image Error]"); }

        // Labels
        JLabel title = createLabel(movie.getTitle(), new Font("SansSerif", Font.BOLD, 28), Color.BLACK);
        JLabel info = createLabel(showtime.getStartTime() + " | Seats: " + String.join(", ", selectedSeats),
                new Font("SansSerif", Font.PLAIN, 22), Color.DARK_GRAY);
        JLabel price = createLabel("Total: €" + String.format("%.2f", totalPrice),
                new Font("SansSerif", Font.BOLD, 26), new Color(34, 150, 243));

        // Confirm Button
        JButton confirmBtn = new JButton("Confirm & Pay Now");
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setBackground(new Color(46, 204, 113));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());

        // Assembly
        content.add(posterLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(title);
        content.add(info);
        content.add(Box.createVerticalStrut(20));
        content.add(price);
        content.add(Box.createVerticalStrut(25));
        content.add(confirmBtn);

        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(5, 0, 5, 0));
        return l;
    }

    private void confirmBooking() {
        if (!UserSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to continue.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Confirm payment?", "Payment", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check availability
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

            // Insert Booking
            String insertSQL = "INSERT INTO tickets (user_id, screening_id, seat_id, status) VALUES (?, ?, ?, 'BOOKED')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                for (int sid : selectedSeatIds) {
                    insertStmt.setInt(1, UserSession.getUserId());
                    insertStmt.setInt(2, showtime.getId());
                    insertStmt.setInt(3, sid);

                    insertStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Booking Confirmed! Enjoy your movie!");
                this.dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void goBackToSeats() {
        new SeatSelectionPage(hallId, movie.getId(), screeningId, selectedSeats).setVisible(true);
        this.dispose();
    }
}