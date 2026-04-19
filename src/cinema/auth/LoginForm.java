// Package declaration
package cinema.auth;

// Import project-specific classes
import cinema.booking.MovieListingPage;
import cinema.dao.UserDAO;
import cinema.models.User;
import cinema.panels.AdminPanel;

// Import necessary libraries for GUI (Swing/AWT) and Database (SQL)
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Provides a login interface for the cinema system.
 * Users enter email and password to authenticate.
 * Redirects users based on role to {@link cinema.booking.MovieListingPage} or {@link cinema.panels.AdminPanel}.
 */
public class LoginForm extends JFrame {

    /** Text field for user email. */
    private JTextField emailField = new JTextField(25);
    /** Password field for user password. */
    private JPasswordField passwordField = new JPasswordField(25);

    /**
     * Font used for labels across forms.
     */
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

    /**
     * Font used for input fields across forms.
     */
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructs the LoginForm window and initializes all UI components.
     */
    public LoginForm() {
        setupWindow();
        setupMainContent();
    }

    /**
     * Configures the main JFrame properties such as title, close operation,
     * and background color.
     */
    private void setupWindow() {
        // Basic window setup
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
    }

    /**
     * Builds and assembles the main UI layout by combining form, action,
     * and navigation link panels into a single container.
     */
    private void setupMainContent() {
        // Main container: Uses BorderLayout and adds padding (EmptyBorder)
        JPanel mainContent = new JPanel(new BorderLayout(15, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        mainContent.add(createFormPanel(), BorderLayout.NORTH);
        mainContent.add(createActionPanel(), BorderLayout.CENTER);
        mainContent.add(createLinksPanel(), BorderLayout.SOUTH);
        // Add the assembled container to the JFrame
        add(mainContent);
        // Window finalization: size to fit, set minimum bounds, and center on screen
        pack();
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null);
    }

    /**
     * Creates the form section containing email and password input fields.
     */
    private JPanel createFormPanel() {
        // Form panel: Organized in a grid (4 rows, 1 column) for labels and fields
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Add email and password inputs using the helper styling method
        addStyledField(formPanel, "Email:", emailField);
        addStyledField(formPanel, "Password:", passwordField);
        return formPanel;
    }

    /**
     * Creates the login button panel and attaches the authentication handler.
     */
    private JPanel createActionPanel() {
        // Login Button: Instantiated, styled, and linked to the login logic
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> loginUser());

        // Set button size to match the input fields for visual balance
        loginButton.setPreferredSize(new Dimension(400, 40));

        // Button Panel: Uses GridBagLayout to keep the button centered
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.add(loginButton);
        return panel;
    }

    /**
     * Creates navigation links for password reset and user registration.
     */
    private JPanel createLinksPanel() {
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
                new RegistrationForm().setVisible(true);
                dispose();
            }
        });
        linksPanel.add(changePassLabel);
        linksPanel.add(registerLabel);
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
        btn.setBackground(new Color(34, 150, 243)); // Modern blue color
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
     * Performs user authentication using email and password.
     *
     * This method validates the credentials through {@link cinema.dao.UserDAO}.
     * If authentication is successful, the user session is created and the user
     * is redirected to the main movie listing page.
     *
     * Admin users are additionally granted access to the admin panel.
     */
    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword()); // Get password as String

        try {
            // Create DAO instance and attempt to authenticate user using provided credentials
            UserDAO userDAO = new UserDAO();
            User user = userDAO.loginUser(email, password);

            if (user != null) {
                // Store authenticated user in session for later access across the application
                UserSession.setUserId(user.getId());

                // Extract data from the user object
                String name = user.getName();
                String role = user.getRole();

                this.dispose();
                // Open main movie page for all users
                new MovieListingPage().setVisible(true);

                // If the user is an admin
                if ("ADMIN".equals(role)) {
                    // Open admin panel window
                    new AdminPanel().setVisible(true);
                    JOptionPane.showMessageDialog(this, "Welcome ADMIN, " + name + "!");

                } else { // If the user is NOT an admin
                    JOptionPane.showMessageDialog(this, "Welcome, " + name + "!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
