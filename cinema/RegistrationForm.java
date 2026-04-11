package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * RegistrationForm class provides a graphical user interface for new user sign-up.
 */
public class RegistrationForm extends JFrame {
    // UI components for user input
    private JTextField emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField fullNameField;

    public RegistrationForm() {
        // Basic window configuration
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container panel setup
        JPanel mainPanel = new JPanel();
        // GridBagLayout is used for precise control over component rows and columns
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        // GridBagConstraints defines where and how components are placed within the GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Exterior padding for each component
        gbc.anchor = GridBagConstraints.WEST;     // Align labels and fields to the left

        // --- Email Input Section ---
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        mainPanel.add(emailLabel, gbc);
        gbc.gridx = 1; // Column 1
        mainPanel.add(emailField, gbc);

        // --- Full Name Input Section ---
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1; // Row 1
        mainPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        // --- Password Input Section ---
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 2; // Row 2
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // --- Confirm Password Input Section ---
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 3; // Row 3
        mainPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // --- Submit Button Section ---
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(34, 150, 243)); // Modern blue theme
        registerButton.setForeground(Color.WHITE);            // White text
        registerButton.setFocusPainted(false);

        // Attach event listener to trigger the registration logic
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 4; // Bottom row
        mainPanel.add(registerButton, gbc);

        // Finalize frame setup
        this.add(mainPanel);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);  // Center the window on the desktop
    }

    /**
     * Handles the logic for validating input and saving user data to the database.
     */
    private void registerUser() {
        // Extracting and trimming whitespace from inputs
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();

        try {
            // 1. Input Validation: Checks format and existence via custom Validator class
            InputValidator.validateEmail(email, 0);
            InputValidator.validateFullName(fullName);

            // 2. Logic Check: Ensure passwords are provided and match
            if (password.isEmpty()) {
                throw new InvalidInputException("Password cannot be empty.");
            }
            if (!password.equals(confirmPassword)) {
                throw new InvalidInputException("Passwords do not match!");
            }

            // 3. Database Interaction
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);

                // Binding parameters to prevent SQL Injection
                pst.setString(1, email);
                pst.setString(2, password);
                pst.setString(3, fullName);
                pst.setString(4, "USER");

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");

                // Transition to Log in screen and close registration window
                new LoginForm().setVisible(true);
                this.dispose();
            }
        } catch (InvalidInputException ex) {
            // Displays specific validation error messages to the user
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            // Handles connectivity or query issues
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm().setVisible(true);
            }
        });
    }
}