package cinema.panels;

import cinema.DatabaseConnection;
import cinema.InputValidator;
import cinema.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public UsersPanel() {
        setLayout(new BorderLayout());

        // Table Setup
        String[] columns = {"id", "name", "email", "password", "role"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action Listeners
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> showUserDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) showUserDialog(row);
            else JOptionPane.showMessageDialog(this, "Select a row to edit");
        });
        deleteBtn.addActionListener(e -> deleteUser());

        // Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData(); // Initial load
    }

    private void loadData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("password"), rs.getString("role")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void showUserDialog(Integer selectedRow) {
        boolean isEdit = (selectedRow != null);
        JTextField nameF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 1) : "");
        JTextField emailF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 2) : "");
        JPasswordField passF = new JPasswordField(isEdit ? (String) model.getValueAt(selectedRow, 3) : "");
        JTextField roleF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 4) : "");

        Object[] fields = {"Name:", nameF, "Email:", emailF, "Password:", passF, "Role:", roleF};
        int option = JOptionPane.showConfirmDialog(this, fields, isEdit ? "Edit User" : "Add User", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String pass = new String(passF.getPassword()).trim();
            String role = roleF.getText().trim();
            int userId = isEdit ? (int) model.getValueAt(selectedRow, 0) : 0;

            try {
                // Validation Logic
                InputValidator.validateNonEmpty(name, "Name");
                InputValidator.validateNonEmpty(email, "Email");
                InputValidator.validateNonEmpty(pass, "Password");
                InputValidator.validateNonEmpty(role, "Role");
                InputValidator.validateEmail(email, userId);
                InputValidator.validateFullName(name);
                InputValidator.validateRole(role);

                saveUser(userId, name, email, pass, role, isEdit);
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void saveUser(int id, String name, String email, String pass, String role, boolean isEdit) {
        String confirmMsg = "Are you sure you want to " + (isEdit ? "update" : "add") + " this user?";
        if (JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        String sql = isEdit ? "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?"
                : "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);
            if (isEdit) pst.setInt(5, id);

            pst.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Success!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                pst.setInt(1, (int) model.getValueAt(row, 0));
                pst.executeUpdate();
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
            }
        }
    }
}