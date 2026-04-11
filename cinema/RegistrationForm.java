package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField fullNameField;

    public RegistrationForm() {
        // Set the window title
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the main panel with GridBagLayout for flexible positioning
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());  // GridBagLayout allows flexible control over components placement
        mainPanel.setBackground(Color.WHITE);

        // GridBag constraints object to control component positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add some space around each component
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left

        // Create email field
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        gbc.gridx = 0; // Set column position
        gbc.gridy = 0; // Set row position
        mainPanel.add(emailLabel, gbc);
        gbc.gridx = 1;  // Move to the next column
        mainPanel.add(emailField, gbc);

        // Create full name field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1; // Move to next row
        mainPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        // Create password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Create confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Create register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(34, 150, 243)); // Button color
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // Add register button to the layout with GridBagLayout constraints
        gbc.gridx = 1;
        gbc.gridy = 4;  // Place button below the last input field
        mainPanel.add(registerButton, gbc);

        // Add the main panel to the window
        this.add(mainPanel);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);  // Center the window on the screen
    }

    private void registerUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();

        try {
            // 1. Let the validator handle the format AND the empty check
            InputValidator.validateEmail(email, 0);
            InputValidator.validateFullName(fullName);

            // 2. Simple logic checks for passwords
            if (password.isEmpty()) {
                throw new InvalidInputException("Password cannot be empty.");
            }
            if (!password.equals(confirmPassword)) {
                throw new InvalidInputException("Passwords do not match!");
            }

            // 3. Database logic
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, email);
                pst.setString(2, password); // Note: Always hash passwords in production!
                pst.setString(3, fullName);
                pst.setString(4, "USER");

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");

                new LoginForm().setVisible(true);
                this.dispose();
            }
        } catch (InvalidInputException ex) {
            // This catches all your custom validation errors in one place
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create and display the registration form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm().setVisible(true);
            }
        });
    }
}
