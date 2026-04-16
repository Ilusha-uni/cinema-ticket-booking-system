// Package declaration
package cinema.auth;

// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.exception.InputValidator;
import cinema.exception.InvalidInputException;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * RegistrationForm class creates a graphical user interface for new users to sign up.
 * It follows the same structural format and styling as LoginForm.
 */
public class RegistrationForm extends JFrame {

    // UI Components
    private JTextField emailField = new JTextField(25);
    private JTextField fullNameField = new JTextField(25);
    private JPasswordField passwordField = new JPasswordField(25);
    private JPasswordField confirmPasswordField = new JPasswordField(25);

    // Styling constants
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    public RegistrationForm() {
        // Basic window setup
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        // Main container: Uses BorderLayout and adds padding
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Form panel: Organized in a grid (8 rows: 4 labels + 4 fields)
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Add fields using the helper styling method
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Full Name:", fullNameField);
        addStyledField(formPanel, "Password:", passwordField);
        addStyledField(formPanel, "Confirm Password:", confirmPasswordField);

        // Register Button
        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> registerUser());
        registerButton.setPreferredSize(new Dimension(400, 45));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(registerButton);

        // Links Panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linksPanel.setBackground(Color.WHITE);

        JLabel loginLabel = new JLabel("Already have an account? Login here", SwingConstants.CENTER);
        styleLinkLabel(loginLabel);
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
        linksPanel.add(loginLabel);

        // Assemble all sub-panels
        mainContent.add(formPanel, BorderLayout.NORTH);
        mainContent.add(buttonPanel, BorderLayout.CENTER);
        mainContent.add(linksPanel, BorderLayout.SOUTH);

        add(mainContent);

        // Window finalization
        pack();
        setMinimumSize(new Dimension(450, 600));
        setLocationRelativeTo(null);
    }

    /**
     * Helper method to style and add labels/fields to a panel (Matches LoginForm).
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
     * Helper method to apply colors, fonts, and cursors to buttons (Matches LoginForm).
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(34, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    /**
     * Helper method to make JLabels look like clickable hyperlinks (Matches LoginForm).
     */
    private void styleLinkLabel(JLabel label) {
        label.setForeground(Color.BLUE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Core logic for user registration.
     */
    private void registerUser() {
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        try {
            // Validation
            InputValidator.validateEmail(email, 0);
            InputValidator.validateFullName(fullName);

            if (password.isEmpty()) throw new InvalidInputException("Password cannot be empty.");
            if (!password.equals(confirmPassword)) throw new InvalidInputException("Passwords do not match!");
            if (password.length() < 6) throw new InvalidInputException("Password must be at least 6 characters long.");

            // Database Insertion
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, email);
                pst.setString(2, password);
                pst.setString(3, fullName);
                pst.setString(4, "USER");

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");

                // Redirect to Login
                new LoginForm().setVisible(true);
                this.dispose();
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
}
