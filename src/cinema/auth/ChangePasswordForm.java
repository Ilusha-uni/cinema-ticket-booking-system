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
 * Provides a user interface for updating an existing user's password.
 *
 * It validates user input, enforces password rules, and updates the password
 * through {@link cinema.dao.UserDAO}.
 *
 * The user must enter their current password for verification before a new password is accepted.
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

    /**
     * Font used for labels in the UI.
     */
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

    /**
     * Font used for input fields in the UI.
     */
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructs the ChangePasswordForm GUI and initializes all components.
     */
    public ChangePasswordForm() {
        setupWindow();
        setupMainContent();
    }

    /**
     * Configures the JFrame properties including title, close operation,
     * and basic UI appearance.
     */
    private void setupWindow() {
        // Basic window configuration
        setTitle("Change Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
    }

    /**
     * Builds and assembles the main UI layout for the password change form.
     * Includes form inputs and the update button.
     */
    private void setupMainContent() {
        // mainContent: The primary container using BorderLayout for flexible spacing
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25)); // Internal padding

        // formPanel: Uses createFormPanel
        JPanel formPanel = createFormPanel();

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
     * Creates the form panel containing all input fields required for password change.
     * This includes email, current password, new password, and confirm password fields.
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Adding labeled input fields using a helper method to maintain clean code
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Current Password:", oldPasswordField);
        addStyledField(formPanel, "New Password:", newPasswordField);
        addStyledField(formPanel, "Confirm New Password:", confirmPasswordField);
        return formPanel;
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
     * Handles password update for an existing user.
     *
     * This method validates all input fields, ensures password rules are met,
     * and updates the password in the database using {@link cinema.dao.UserDAO}.
     *
     * The user must provide their current password for verification before
     * a new password can be set. Appropriate success or error messages are shown.
     */
    private void updatePassword() {
        // Extract user input from form fields
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

            // Enforce password rules
            if (newPass.length() < 6) {
                throw new InvalidInputException("New password must be at least 6 characters long.");
            }
            if (!newPass.equals(confirmPass)) {
                throw new InvalidInputException("New passwords do not match!");
            }
            // DAO layer handles database update operation
            UserDAO userDAO = new UserDAO();
            // Attempt to update password in database
            boolean passwordChangedSuccess = userDAO.changePassword(email, oldPass, newPass);

            if (passwordChangedSuccess) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                // Redirect user back to login screen after successful update
                this.dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email or current password is incorrect.");
            }
        } catch (InvalidInputException ex) {
            // Handle validation errors (user input mistakes)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Handle unexpected system or database errors
            JOptionPane.showMessageDialog(this, "Something went wrong. Please try again.");
        }
    }
}
