// Package declaration
package cinema.auth;

// Import project-specific classes
import cinema.exception.InputValidator;
import cinema.exception.InvalidInputException;
import cinema.dao.UserDAO;

// Import necessary libraries for GUI (Swing/AWT)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Provides a password change interface for existing users.
 * Validates current password and ensures new passwords match.
 * Updates password using {@link cinema.dao.UserDAO}.
 */
public class ChangePasswordForm extends JFrame {

    /** The input field for the user's current password. */
    private JPasswordField oldPasswordField = new JPasswordField(25);

    /** The input field for the user's new password. */
    private JPasswordField newPasswordField = new JPasswordField(25);

    /** The input field for confirming the new password. */
    private JPasswordField confirmPasswordField = new JPasswordField(25);

    /** The input field for the user's email address. */
    private JTextField emailField = new JTextField(25);

    // Constant colors for a consistent UI theme
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructs the ChangePasswordForm GUI and initializes all components.
     */
    public ChangePasswordForm() {
        // Basic window configuration
        setTitle("Change Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
     * Adds a label and text field to the specified panel, applying consistent styling.
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
     * Styles a JButton to match the application's design guidelines.
     * Sets font, background color, foreground color, cursor, size, and disables focus painting.
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
     * Validates input and updates the password in the database.
     * Shows messages for success, errors, or invalid input.
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
            UserDAO userDAO = new UserDAO();
            boolean passwordChangedSuccess = userDAO.changePassword(email, oldPass, newPass);

            if (passwordChangedSuccess) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                // Clean up and redirect user back to login
                this.dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email or current password is incorrect.");
            }
        } catch (InvalidInputException ex) {
            // Show custom error messages to the user
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Something went wrong. Please try again.");
        }
    }
}
