package cinema.panels;

import cinema.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.regex.Pattern;

public class UsersPanel extends JPanel {

    public UsersPanel() {
        setLayout(new BorderLayout());

        // Define the columns for the JTable
        String[] columns = {"id", "name", "email", "role"};
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

            Object[] fields = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String role = roleField.getText().trim();

                // Validate input fields
                if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                // Validate email format
                if (!Pattern.matches(emailRegex, email)) {
                    JOptionPane.showMessageDialog(this, "Invalid email format. Please enter a valid email address.");
                    return;
                }

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "INSERT INTO users (name, email, role) VALUES (?, ?, ?)";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setString(2, email);
                    pst.setString(3, role);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                    refresh.doClick();  // Refresh the table
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
            String role = (String) model.getValueAt(row, 3);

            JTextField nameField = new JTextField(name);
            JTextField emailField = new JTextField(email);
            JTextField roleField = new JTextField(role);

            Object[] fields = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newRole = roleField.getText().trim();

                // Validate input fields
                if (newName.isEmpty() || newEmail.isEmpty() || newRole.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                // Validate email format
                if (!Pattern.matches(emailRegex, newEmail)) {
                    JOptionPane.showMessageDialog(this, "Invalid email format. Please enter a valid email address.");
                    return;
                }

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, newName);
                    pst.setString(2, newEmail);
                    pst.setString(3, newRole);
                    pst.setInt(4, id);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                    refresh.doClick();  // Refresh the table
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
