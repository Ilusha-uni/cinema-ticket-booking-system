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
        // Retrieve the user input
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText();

        try {
            // Validate email format
            if (email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty.");
                return;
            } else {
                InputValidator.validateEmail(email);  // Validate email format
            }

            // Validate full name format
            if (fullName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full Name cannot be empty.");
                return;
            } else {
                InputValidator.validateFullName(fullName); // Validate full name format
            }

            // Validate password
            if (password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }

            // Validate confirm password
            if (confirmPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Confirm Password cannot be empty.");
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            // Now, general check for empty fields (after format validation)
            if (email.trim().isEmpty() || password.trim().isEmpty() || fullName.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }



            // Insert the data into the database
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, email);
                pst.setString(2, password);  // Better to hash the password before storing it
                pst.setString(3, fullName);
                pst.setString(4, "USER");  // Default role for a new user is "USER"
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");

                // Redirect to the login page after successful registration
                new LoginForm().setVisible(true); // Show the login page
                this.dispose(); // Close the current registration window

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
