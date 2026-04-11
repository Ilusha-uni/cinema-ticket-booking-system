// Define the package name for the cinema application
package cinema;

// Import Swing components for the GUI
import javax.swing.*;
// Import AWT classes for layout, colors, and events
import java.awt.*;
import java.awt.event.*;
// Import SQL classes for database connectivity
import java.sql.*;

// Main class for the Login window, extending JFrame
public class LoginForm extends JFrame {
    // UI components for user input: email and password
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);

    // Constructor to initialize the login form
    public LoginForm() {
        // Set basic frame properties
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Used to center the content panel within the window
        getContentPane().setBackground(Color.WHITE);

        // Create a sub-panel for the input fields using a grid layout (4 rows, 1 column)
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);

        // Add email label and input field to the panel
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        // Add password label and input field to the panel
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        // Initialize and style the Login button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        // Attach action listener to trigger the login logic when clicked
        loginButton.addActionListener(e -> loginUser());

        // Create a clickable label for users who don't have an account
        JLabel registerLabel = new JLabel("Don't have an account? Register here", SwingConstants.CENTER);
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Open RegistrationForm and close the current window when clicked
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegistrationForm().setVisible(true);
                dispose();
            }
        });

        // Create a container to hold the input fields and the login button
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(loginButton, BorderLayout.SOUTH);

        // Create a final wrapper to include the registration link at the bottom
        JPanel finalWrapper = new JPanel(new BorderLayout(10, 20));
        finalWrapper.setBackground(Color.WHITE);
        finalWrapper.add(container, BorderLayout.CENTER);
        finalWrapper.add(registerLabel, BorderLayout.SOUTH);

        // Finalize frame setup: add wrapper, set size, and center on screen
        add(finalWrapper);
        setSize(500, 450);
        setLocationRelativeTo(null);
    }

    // Method to apply consistent styling to buttons
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(34, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    /**
     * Logic to handle user authentication
     */
    private void loginUser() {
        // Retrieve credentials from text fields
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Attempt to connect to the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Prepare SQL query to check credentials
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            // If a record is found, the user is authenticated
            if (rs.next()) {
                // Store user ID in a session and retrieve the user's role
                UserSession.setUserId(rs.getInt("id"));
                String role = rs.getString("role");

                // Navigate to the main movie listing page
                new MovieListingPage().setVisible(true);
                this.dispose();

                // Branch logic based on user role (Admin vs Regular User)
                if ("ADMIN".equals(role)) openAdminPanel();
                else openUserPanel();
            } else {
                // Show error if no matching record is found
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } catch (SQLException ex) {
            // Show error message if database communication fails
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Helper method to transition to the Admin interface
    private void openAdminPanel() {
        JOptionPane.showMessageDialog(this, "Welcome, ADMIN!");
        new AdminPanel().setVisible(true);
        this.dispose();
    }

    // Helper method for standard user login confirmation
    private void openUserPanel() {
        JOptionPane.showMessageDialog(this, "Welcome, User!");
    }

    // Application entry point
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}