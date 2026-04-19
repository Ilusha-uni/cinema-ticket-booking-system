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
import java.awt.event.*;

/**
 * Provides a user registration interface for the cinema system.
 * Users can enter email, full name, and password to create a new account.
 * Validates input using {@link cinema.exception.InputValidator} and registers users via {@link cinema.dao.UserDAO}.
 * Redirects to {@link cinema.auth.LoginForm} upon successful registration.
 */
public class RegistrationForm extends JFrame {

    /** Text field for user email. */
    private JTextField emailField = new JTextField(25);
    /** Text field for full name. */
    private JTextField fullNameField = new JTextField(25);
    /** Password field for password. */
    private JPasswordField passwordField = new JPasswordField(25);
    /** Password field for confirming password. */
    private JPasswordField confirmPasswordField = new JPasswordField(25);

    /**
     * Font used for form labels in the UI.
     */
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

    /**
     * Font used for input fields in the UI.
     */
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructs the RegistrationForm window and initializes all UI components.
     */
    public RegistrationForm() {
        setupWindow();
        setupMainContent();
    }

    /**
     * Configures the JFrame properties such as title, close operation,
     * and background styling.
     */
    private void setupWindow() {
        // Basic window setup
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

    }

    /**
     * Builds and assembles the main registration UI including form,
     * button, and navigation link panels.
     */
    private void setupMainContent() {
        // Main container: Uses BorderLayout and adds padding
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Assemble all sub-panels
        mainContent.add(createFormPanel(), BorderLayout.NORTH);
        mainContent.add(createButtonPanel(), BorderLayout.CENTER);
        mainContent.add(createLinksPanel(), BorderLayout.SOUTH);
        // Add the assembled container to the JFrame
        add(mainContent);

        // Window size that fits the screen, set minimum bounds, and center on screen
        pack();
        setMinimumSize(new Dimension(450, 600));
        setLocationRelativeTo(null);
    }

    /**
     * Creates the registration form panel containing input fields
     * for email, full name, password, and confirm password.
     *
     * @return JPanel containing the styled form inputs
     */
    private JPanel createFormPanel() {
        // Form panel: Organized in a grid (8 rows: 4 labels + 4 fields)
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Add fields using the helper styling method
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Full Name:", fullNameField);
        addStyledField(formPanel, "Password:", passwordField);
        addStyledField(formPanel, "Confirm Password:", confirmPasswordField);
        return formPanel;
    }

    /**
     * Creates the registration button panel and attaches the registration logic.
     *
     * @return JPanel containing the register button
     */
    private JPanel createButtonPanel() {
        // Register Button
        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> registerUser());
        registerButton.setPreferredSize(new Dimension(400, 45));

        // Button Panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.add(registerButton);

        return panel;
    }

    /**
     * Creates navigation links for switching back to the login screen.
     *
     * @return JPanel containing login navigation link
     */
    private JPanel createLinksPanel() {
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
        return linksPanel;
    }

    /**
     * Styles and adds a label and text field to a panel.
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
     * Styles a button with colors, font, cursor, and focus behavior.
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
     * Styles a label to appear as a clickable hyperlink.
     */
    private void styleLinkLabel(JLabel label) {
        label.setForeground(Color.BLUE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Handles user registration process.
     *
     * Validates all input fields, checks for existing email,
     * and registers the user through {@link cinema.dao.UserDAO}.
     *
     * Displays success or error messages based on the result.
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

            if (password.isEmpty())
                throw new InvalidInputException("Password cannot be empty.");
            if (!password.equals(confirmPassword))
                throw new InvalidInputException("Passwords do not match!");
            if (password.length() < 6)
                throw new InvalidInputException("Password must be at least 6 characters long.");

            // DAO responsible for database operations
            UserDAO userDAO = new UserDAO();

            // Check if email already exists in the database
            if (userDAO.emailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already exists!");
                return;
            }
            // Attempt to register user in database
            boolean registrationSuccess = userDAO.registerUser(email, password, fullName);

            if (registrationSuccess) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                // Redirect to Login page
                new LoginForm().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        } catch (InvalidInputException ex) { // Handle validation-related errors
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {// Handle unexpected system/database errors
            JOptionPane.showMessageDialog(this, "Something went wrong. Please try again.");
        }
    }
}
