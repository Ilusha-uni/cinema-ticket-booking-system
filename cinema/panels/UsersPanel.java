package cinema.panels;

import cinema.DatabaseConnection;
import cinema.InputValidator;
import cinema.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.regex.Pattern;

public class UsersPanel extends JPanel {

    public UsersPanel() {
        setLayout(new BorderLayout());

        // Define the columns for the JTable, placing 'password' after 'email'
        String[] columns = {"id", "name", "email", "password", "role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        // Create the buttons
        JButton refresh = new JButton("Refresh");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Regular expression for validating email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Refresh button action: loads the data from the database
        refresh.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

                model.setRowCount(0);  // Clear the table

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),  // Password comes after email
                            rs.getString("role")
                    });
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Add button action: opens a dialog to add a new user
        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField roleField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            Object[] fields = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Password:", passwordField,
                    "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String role = roleField.getText().trim();

                try {
                    // Perform validation using InputValidator methods
                    InputValidator.validateNonEmpty(name, "Name");
                    InputValidator.validateNonEmpty(email, "Email");
                    InputValidator.validateNonEmpty(password, "Password");
                    InputValidator.validateNonEmpty(role, "Role");
                    InputValidator.validateEmail(email, 0);  // Validate email format and uniqueness (userId = 0 for add)
                    InputValidator.validateFullName(name);  // Validate full name format
                    InputValidator.validateRole(role);  // Validate role

                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to add this user?", "Confirm Add", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, name);
                        pst.setString(2, email);
                        pst.setString(3, password);  // Store password
                        pst.setString(4, role);

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(this, "User added successfully!");
                        refresh.doClick();  // Refresh the table
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // Edit button action: opens a dialog to edit an existing user
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to edit");
                return;
            }

            int id = (int) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            String email = (String) model.getValueAt(row, 2);
            String password = (String) model.getValueAt(row, 3);  // Get password from table
            String role = (String) model.getValueAt(row, 4);

            JTextField nameField = new JTextField(name);
            JTextField emailField = new JTextField(email);
            JPasswordField passwordField = new JPasswordField(password);
            JTextField roleField = new JTextField(role);

            Object[] fields = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Password:", passwordField,
                    "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newPassword = new String(passwordField.getPassword()).trim();
                String newRole = roleField.getText().trim();
                int userId = (int) model.getValueAt(row, 0);  // Get the current user's ID

                try {
                    // Perform validation using InputValidator methods
                    InputValidator.validateNonEmpty(newName, "Name");
                    InputValidator.validateNonEmpty(newEmail, "Email");
                    InputValidator.validateNonEmpty(newPassword, "Password");
                    InputValidator.validateNonEmpty(newRole, "Role");
                    InputValidator.validateEmail(newEmail, userId);  // Pass the user ID for edit validation
                    InputValidator.validateFullName(newName);  // Validate full name format
                    InputValidator.validateRole(newRole);  // Validate role

                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to update this user?", "Confirm Edit", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ? WHERE id = ?";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, newName);
                        pst.setString(2, newEmail);
                        pst.setString(3, newPassword);  // Update password
                        pst.setString(4, newRole);
                        pst.setInt(5, id);

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(this, "User updated successfully!");
                        refresh.doClick();  // Refresh the table
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // Delete button action: deletes a user from the database
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to delete");
                return;
            }

            int id = (int) model.getValueAt(row, 0);

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM users WHERE id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    refresh.doClick();  // Refresh the table
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // Layout for the panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refresh);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial table load
        refresh.doClick();

        // Add the final panel to the current panel
        add(panel, BorderLayout.CENTER);
    }
}