package cinema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Used only to center the content panel
        getContentPane().setBackground(Color.WHITE);

        // Create a central panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);

        // Email Section
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        // Password Section
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> loginUser());

        // Register Link
        JLabel registerLabel = new JLabel("Don't have an account? Register here", SwingConstants.CENTER);
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegistrationForm().setVisible(true);
                dispose();
            }
        });

        // Add components to frame
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(Color.WHITE);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(loginButton, BorderLayout.SOUTH);

        // Wrap everything to add the register link at the very bottom
        JPanel finalWrapper = new JPanel(new BorderLayout(10, 20));
        finalWrapper.setBackground(Color.WHITE);
        finalWrapper.add(container, BorderLayout.CENTER);
        finalWrapper.add(registerLabel, BorderLayout.SOUTH);

        add(finalWrapper);
        setSize(500, 450);
        setLocationRelativeTo(null);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(34, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                UserSession.setUserId(rs.getInt("id"));
                String role = rs.getString("role");

                new MovieListingPage().setVisible(true);
                this.dispose();

                if ("ADMIN".equals(role)) openAdminPanel();
                else openUserPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openAdminPanel() {
        JOptionPane.showMessageDialog(this, "Welcome, ADMIN!");
        new AdminPanel().setVisible(true);
        this.dispose();
    }

    private void openUserPanel() {
        JOptionPane.showMessageDialog(this, "Welcome, User!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}