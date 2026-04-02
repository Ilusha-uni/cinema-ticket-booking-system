package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginForm() {
        // Set the window title
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the main panel with GridBagLayout for flexible positioning
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());  // Use GridBagLayout for flexible positioning
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

        // Create password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Create login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(34, 150, 243)); // Button color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        // Add login button to the layout with GridBagLayout constraints
        gbc.gridx = 1;
        gbc.gridy = 2;  // Place button below the password field
        mainPanel.add(loginButton, gbc);

        // Add "Register" link to go to the registration form
        JLabel registerLabel = new JLabel("<HTML><U>Don't have an account? Register here</U></HTML>");
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter()   {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate to the Registration Form
                new RegistrationForm().setVisible(true);
                dispose();  // Close the current login form
            }
        });

        // Add register label to the layout
        gbc.gridx = 1;
        gbc.gridy = 3;  // Place below the login button
        mainPanel.add(registerLabel, gbc);

        // Add the main panel to the window
        this.add(mainPanel);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);  // Center the window on the screen
    }

    private void loginUser() {
        // Retrieve the user input
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL query to fetch user details based on email and password
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            // If a user is found
            if (rs.next()) {
                // Get the user's ID and role
                int userId = rs.getInt("id");
                String role = rs.getString("role");

                // Store the user ID in the session
                UserSession.setUserId(userId);

                // Redirect to movie listing page after successful login
                new MovieListingPage().setVisible(true); // Show movie listing page
                this.dispose(); // Close the current login window

                // If the user is an admin
                if ("ADMIN".equals(role)) {
                    // Open admin panel (could be another frame or window)
                    openAdminPanel();
                } else {
                    // Open user panel (could be another frame or window)
                    openUserPanel();
                }
            } else {
                // Show an error if credentials are incorrect
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openAdminPanel() {
        // This function should open the admin panel
        JOptionPane.showMessageDialog(this, "Welcome, Admin!");
        // You can create a new JFrame or open a new window for the admin
    }

    private void openUserPanel() {
        // This function should open the user panel
        JOptionPane.showMessageDialog(this, "Welcome, User!");
        // You can create a new JFrame or open a new window for the user
    }

    public static void main(String[] args) {
        // Create and display the login form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}