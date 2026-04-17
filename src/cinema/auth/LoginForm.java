// Package declaration
package cinema.auth;

// Import project-specific classes
import cinema.booking.MovieListingPage;
import cinema.database.DatabaseConnection;
import cinema.panels.AdminPanel;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * LoginForm class creates a graphical user interface for users to log into the cinema system.
 * It extends JFrame to create the main window.
 */
public class LoginForm extends JFrame {

    // UI Components: Text fields for user input
    private JTextField emailField = new JTextField(25);
    private JPasswordField passwordField = new JPasswordField(25);

    // Styling constants: Defined to ensure consistency across different forms
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    public LoginForm() {
        setupWindow();
        setupMainContent();
    }
    private void setupWindow() {
        // Basic window setup
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
    }
    private void setupMainContent() {
        // Main container: Uses BorderLayout and adds padding (EmptyBorder)
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Form panel: Organized in a grid (4 rows, 1 column) for labels and fields
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Add email and password inputs using the helper styling method
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Password:", passwordField);

        // Login Button: Instantiated, styled, and linked to the login logic
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> loginUser());

        // Set button size to match the input fields for visual balance
        loginButton.setPreferredSize(new Dimension(400, 40));

        // Button Panel: Uses GridBagLayout to keep the button centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(loginButton);

        // Links Panel: Contains "Forgot Password" and "Register" labels
        JPanel linksPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        linksPanel.setBackground(Color.WHITE);

        // Forgot Password link logic
        JLabel changePassLabel = new JLabel("Forgot Password? Change it here", SwingConstants.LEFT);
        styleLinkLabel(changePassLabel);
        changePassLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ChangePasswordForm().setVisible(true); // Open password change window
                dispose(); // Close current login window
            }
        });

        // Registration link logic
        JLabel registerLabel = new JLabel("Don't have an account? Register here", SwingConstants.LEFT);
        styleLinkLabel(registerLabel);
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegistrationForm().setVisible(true); // Open registration window
                dispose(); // Close current login window
            }
        });

        linksPanel.add(changePassLabel);
        linksPanel.add(registerLabel);

        // Assemble all sub-panels into the main content container
        mainContent.add(formPanel, BorderLayout.NORTH);
        mainContent.add(buttonPanel, BorderLayout.CENTER);
        mainContent.add(linksPanel, BorderLayout.SOUTH);

        // Add the assembled container to the JFrame
        add(mainContent);

        // Window finalization: size to fit, set minimum bounds, and center on screen
        pack();
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null);
    }

    /**
     * Helper method to style and add labels/fields to a panel.
     */
    private void addStyledField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        field.setFont(fieldFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        field.setBackground(Color.WHITE);
        panel.add(label);
        panel.add(field);
    }

    /**
     * Helper method to apply colors, fonts, and cursors to buttons.
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(34, 150, 243)); // Modern blue color
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    /**
     * Helper method to make JLabels look like clickable hyperlinks.
     */
    private void styleLinkLabel(JLabel label) {
        label.setForeground(Color.BLUE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Core logic for user authentication.
     * Connects to the database and verifies credentials.
     */
    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword()); // Get password as String

        // Use try-with-resources to ensure database connection closes automatically
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Prepare a SQL query to prevent SQL Injection
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // If a record is found, store user info in a session
                UserSession.setUserId(rs.getInt("id"));
                String name = rs.getString("name");
                String role = rs.getString("role");

                this.dispose(); //Dispose Login form and open appropriate windows based on role

                // Open main movie page for all users
                new MovieListingPage().setVisible(true);

                // If the user is an admin
                if ("ADMIN".equals(role)) {
                    // Open admin panel window
                    new AdminPanel().setVisible(true);
                    JOptionPane.showMessageDialog(this, "Welcome ADMIN, " + name + "!");
                }
                // If the user is NOT an admin
                else {
                    JOptionPane.showMessageDialog(this, "Welcome, " + name + "!");
                }

            } else {
                // Show error if login credentials are invalid
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } catch (SQLException ex) {
            // Show error message if database connection or query fails
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
