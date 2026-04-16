// Package declaration
package cinema.panels;

// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.exception.InputValidator;
import cinema.exception.InvalidInputException;


//Import standard Java libraries for GUI components (Swing/AWT) and database operations (SQL).
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * UsersPanel is a graphical component used to manage (Create, Read, Update, Delete)
 * user records within the cinema management system.
 */
public class UsersPanel extends JPanel {
    // Member variables for the table and its data model to allow dynamic updates
    private JTable table;
    private DefaultTableModel model;

    /**
     * Constructor: Sets up the visual layout, initializes the table, and creates action buttons.
     */
    public UsersPanel() {
        // Use BorderLayout to easily place the table in the center and buttons at the bottom
        setLayout(new BorderLayout());

        // Define column headers and initialize the table model with zero rows
        String[] columns = {"id", "name", "email", "password", "role"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Initialize CRUD operation buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action: Reload data from the database
        refreshBtn.addActionListener(e -> loadData());

        // Action: Open a blank dialog to add a new user
        addBtn.addActionListener(e -> showUserDialog(null));

        // Action: Open a pre-filled dialog to edit a selected user, or show an error if none selected
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) showUserDialog(row);
            else JOptionPane.showMessageDialog(this, "Select a row to edit");
        });

        // Action: Remove the selected user from the database
        deleteBtn.addActionListener(e -> deleteUser());

        // Group buttons together in a sub-panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        // Add the table (inside a scroll pane) and the button panel to the main panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display data as soon as the panel is created
        loadData();
    }

    /**
     * Fetches all records from the 'users' table and populates the JTable.
     */
    private void loadData() {
        // Using try-with-resources to ensure the database connection and statement are closed automatically
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            // Clear existing rows before loading fresh data
            model.setRowCount(0);

            // Loop through the database results and add them to the table model
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                });
            }
        } catch (SQLException ex) {
            // Display an error message if the SQL query fails
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Opens a modal dialog for either adding or editing a user.
     * @param selectedRow The index of the row to edit, or null if creating a new user.
     */
    private void showUserDialog(Integer selectedRow) {
        boolean isEdit = (selectedRow != null);

        // 1. Create the Dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit User" : "Add User", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // 2. Initialize fields
        JTextField nameF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 1) : "", 20);
        JTextField emailF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 2) : "", 20);
        JPasswordField passF = new JPasswordField(isEdit ? (String) model.getValueAt(selectedRow, 3) : "", 20);
        JTextField roleF = new JTextField(isEdit ? (String) model.getValueAt(selectedRow, 4) : "", 20);

        // 3. Create Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Name:")); formPanel.add(nameF);
        formPanel.add(new JLabel("Email:")); formPanel.add(emailF);
        formPanel.add(new JLabel("Password:")); formPanel.add(passF);
        formPanel.add(new JLabel("Role:")); formPanel.add(roleF);

        // 4. Create Buttons
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");


        // Add action listener to the Save button
        saveBtn.addActionListener(e -> {

	        // Retrieve and clean user input from form fields
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String pass = new String(passF.getPassword()).trim();
            String role = roleF.getText().trim();

            // Determine user ID (only relevant when editing an existing user)
            int userId = isEdit ? (int) model.getValueAt(selectedRow, 0) : 0;

            try {
                // -------- Validation section --------

 		        // Ensure all required fields are filled
                InputValidator.validateNonEmpty(name, "Name");
                InputValidator.validateNonEmpty(email, "Email");
                InputValidator.validateNonEmpty(pass, "Password");
		
		        // Enforce minimum password length
                if (pass.length() < 6) throw new InvalidInputException("Password must be at least 6 characters long.");
		
		        // Validate email,full name,role format
                InputValidator.validateNonEmpty(role, "Role");
                InputValidator.validateEmail(email, userId);
                InputValidator.validateFullName(name);
                InputValidator.validateRole(role);

                // If all validations pass, save the user to the database
                saveUser(userId, name, email, pass, role, isEdit);

		        // Close the dialog after successful save
                dialog.dispose(); 

            } catch (InvalidInputException ex) {
                // Show validation error message to the user
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        // 5. Assemble and Show
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Executes the SQL INSERT or UPDATE statement to persist user data.
     */
    private void saveUser(int id, String name, String email, String pass, String role, boolean isEdit) {
        // Ask for final confirmation before proceeding
        String confirmMsg = "Are you sure you want to " + (isEdit ? "update" : "add") + " this user?";
        if (JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        // Switch SQL syntax based on whether we are updating or inserting
        String sql = isEdit ? "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?"
                : "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            // Map the parameters to the SQL query
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);
            if (isEdit) pst.setInt(5, id);

            // Execute the write operation and refresh the UI
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Success!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }

    /**
     * Removes the selected user from the database after confirmation.
     */
    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
            return;
        }

        int userId = (int) model.getValueAt(row, 0); // Get the user ID from the selected row

        // Check if the user has any tickets booked
        if (hasBookedTickets(userId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete this user. They have booked tickets.");
            return; // Prevent deletion if the user has booked tickets
        }

        // If no tickets are booked, confirm the deletion
        if (JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

                // Use the hidden ID column from the table to identify the record
                pst.setInt(1, (int) model.getValueAt(row, 0));
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
            }
        }
    }

    /**
     * Checks if the user has any booked tickets in the system.
     * @param userId The ID of the user to check.
     * @return true if the user has booked tickets, false otherwise.
     */
    private boolean hasBookedTickets(int userId) {
        // SQL query to check if there are any tickets booked by the user
        String query = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND status = 'BOOKED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, userId); // Set the user ID in the query
            ResultSet rs = pst.executeQuery(); // Execute the query

            if (rs.next()) {
                return rs.getInt(1) > 0; // If the count is greater than 0, the user has booked tickets
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return false; // Default to false if there's an error or no tickets found
    }
}
