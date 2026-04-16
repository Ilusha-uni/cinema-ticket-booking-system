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
import java.sql.*;

/**
 * ChangePasswordForm provides a graphical user interface for users to update their credentials.
 * It handles input validation, UI styling, and database interaction.
 */
public class ChangePasswordForm extends JFrame {

    // UI Components for user input
    // Initialized with column size 25 to ensure the text fields are wide enough for visibility
    private JPasswordField oldPasswordField = new JPasswordField(25);
    private JPasswordField newPasswordField = new JPasswordField(25);
    private JPasswordField confirmPasswordField = new JPasswordField(25);
    private JTextField emailField = new JTextField(25);

    // Reusable font styles for consistency across the form
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    public ChangePasswordForm() {
        // Basic window configuration
        setTitle("Change Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes only this window, not the whole app
        getContentPane().setBackground(Color.WHITE);

        // mainContent: The primary container using BorderLayout for flexible spacing
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25)); // Internal padding

        // formPanel: Uses GridLayout to stack labels and text fields vertically
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Adding labeled input fields using a helper method to maintain clean code
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Current Password:", oldPasswordField);
        addStyledField(formPanel, "New Password:", newPasswordField);
        addStyledField(formPanel, "Confirm New Password:", confirmPasswordField);

        // Action button for submitting the form
        JButton updateButton = new JButton("Update Password");
        styleButton(updateButton);
        // Lambda expression to trigger the update logic when clicked
        updateButton.addActionListener(e -> updatePassword());

        // Layout placement: inputs in center, button at the bottom
        mainContent.add(formPanel, BorderLayout.CENTER);
        mainContent.add(updateButton, BorderLayout.SOUTH);

        add(mainContent);

        // Finalize window dimensions and positioning
        pack(); // Auto-size window based on components
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null); // Centers window on the screen
    }

    /**
     * Helper method to style labels and text fields before adding them to a panel.
     */
    private void addStyledField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);

        field.setFont(fieldFont);
        // Specifically setting the height to 40px for a more modern, accessible feel
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));

        panel.add(label);
        panel.add(field);
    }

    /**
     * Standardizes the look of the "Update" button.
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(34, 150, 243)); // Material Blue
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false); // Removes the ugly focus border around text
    }

    /**
     * Orchestrates the validation logic. If checks pass, it proceeds to DB update.
     */
    private void updatePassword() {
        // Extracting data from fields
        String email = emailField.getText().trim();
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        try {
            // Using a custom Validator class to ensure no fields are empty and email is valid
            InputValidator.validateNonEmpty(email, "Email");
            InputValidator.validateNonEmpty(oldPass, "Current Password");
            InputValidator.validateNonEmpty(newPass, "New Password");
            InputValidator.validateEmail(email, -1);

            // Basic logic for security
            if (newPass.length() < 6) {
                throw new InvalidInputException("New password must be at least 6 characters long.");
            }
            if (!newPass.equals(confirmPass)) {
                throw new InvalidInputException("New passwords do not match!");
            }

            // If all validations pass, communicate with the database
            performDatabaseUpdate(email, oldPass, newPass);
        } catch (InvalidInputException ex) {
            // Show custom error messages to the user
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Connects to the DB to verify current credentials and apply the change.
     */
    private void performDatabaseUpdate(String email, String oldPass, String newPass) {
        // Try-with-resources ensures the connection is closed automatically
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Step 1: Verify if the user exists and the old password is correct
            String verifyQuery = "SELECT password FROM users WHERE email = ?";
            PreparedStatement vPst = conn.prepareStatement(verifyQuery);
            vPst.setString(1, email);
            ResultSet rs = vPst.executeQuery();

            // Password check (Note: In production, passwords should be hashed, not compared as plain text)
            if (rs.next() && rs.getString("password").equals(oldPass)) {

                // Step 2: Update the record with the new password
                String updateQuery = "UPDATE users SET password = ? WHERE email = ?";
                PreparedStatement uPst = conn.prepareStatement(updateQuery);
                uPst.setString(1, newPass);
                uPst.setString(2, email);
                uPst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password updated successfully!");

                // Clean up and redirect user back to login
                this.dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email or current password is incorrect.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
