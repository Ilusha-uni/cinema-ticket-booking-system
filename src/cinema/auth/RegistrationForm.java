package cinema.auth;

import javax.swing.*;

import cinema.database.DatabaseConnection;
import cinema.exception.InputValidator;
import cinema.exception.InvalidInputException;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * RegistrationForm class handles the user sign-up process.
 * It provides a GUI for entering user details and saves them to the database.
 */
public class RegistrationForm extends JFrame {
    // UI Components for user input
    private JTextField emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField fullNameField;

    // Defined fonts to maintain visual consistency across the application
    private Font labelFont = new Font("Segue UI", Font.BOLD, 16);
    private Font fieldFont = new Font("Segue UI", Font.PLAIN, 16);

    public RegistrationForm() {
        // Basic window setup
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container panel using GridBagLayout for precise component positioning
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        // GridBagConstraints defines how components are laid out in the grid
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Margin around elements
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow fields to expand horizontally

        // --- Email Input Section ---
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailField = createStyledField(25);

        gbc.gridx = 0; gbc.gridy = 0; // Position: Column 0, Row 0
        mainPanel.add(emailLabel, gbc);
        gbc.gridx = 1;                // Position: Column 1, Row 0
        mainPanel.add(emailField, gbc);

        // --- Full Name Input Section ---
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(labelFont);
        fullNameField = createStyledField(25);

        gbc.gridx = 0; gbc.gridy = 1; // Row 1
        mainPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        // --- Password Input Section ---
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordField = createStyledPasswordField(25);

        gbc.gridx = 0; gbc.gridy = 2; // Row 2
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // --- Confirm Password Input Section ---
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(labelFont);
        confirmPasswordField =  createStyledPasswordField(25);

        gbc.gridx = 0; gbc.gridy = 3; // Row 3
        mainPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // --- Submit Button Section ---
        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        // Lambda expression to handle the button click event
        registerButton.addActionListener(e -> registerUser());

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Button spans across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerButton, gbc);

        // Finalize frame configuration
        this.add(mainPanel);
        this.pack(); // Size the window to fit components
        this.setMinimumSize(new Dimension(450, 500));
        this.setLocationRelativeTo(null); // Center window on screen
    }

    /**
     * Helper to apply consistent styling to text input fields.
     */
    private JTextField createStyledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(fieldFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }

    /**
     * Helper to apply consistent styling to password fields.
     */
    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(fieldFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }

    /**
     * Helper to apply color, font, and hover-cursor effects to the button.
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segue UI", Font.BOLD, 18));
        btn.setBackground(new Color(34, 150, 243)); // Modern blue color
        btn.setForeground(Color.WHITE);             // White text
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
    }

    /**
     * Validates user input and saves the data to the database.
     */
    private void registerUser() {
        // Retrieve and trim user input
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();

        try {
            // Validation
            InputValidator.validateEmail(email, 0);
            InputValidator.validateFullName(fullName);

            if (password.isEmpty()) {
                throw new InvalidInputException("Password cannot be empty.");
            }
            if (!password.equals(confirmPassword)) {
                throw new InvalidInputException("Passwords do not match!");
            }

            // Database Operation: Establish connection and insert data
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Prepared statement to prevent SQL Injection
                String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, email);
                pst.setString(2, password);
                pst.setString(3, fullName);
                pst.setString(4, "USER"); // Default role

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");

                // Transition to Log in screen
                new LoginForm().setVisible(true);
                this.dispose(); // Close registration window
            }
        } catch (InvalidInputException ex) {
            // Catch custom validation errors
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            // Catch database-related errors
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Main entry point to launch the Registration GUI.
     */
    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
