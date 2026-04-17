// Package declaration
package cinema.panels;

// Import project-specific classes
import cinema.exception.InputValidator;
import cinema.exception.InvalidInputException;
import cinema.dao.UserDAO;
import cinema.models.User;
//Import standard Java libraries for GUI components (Swing/AWT) and database operations (SQL).
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * UsersPanel is a graphical Swing component for managing user records
 * in the cinema management system. It provides CRUD functionality:
 * - Create: Add new users
 * - Read: Display all users in a table
 * - Update: Edit existing users
 * - Delete: Remove users (with checks for booked tickets)
 *
 * This panel communicates with the database through the AdminDAO class
 * and validates user input using the InputValidator utility.
 */
public class UsersPanel extends JPanel {
    /** JTable to display the list of users */
    private JTable table;

    /** Table model to manage table data dynamically */
    private DefaultTableModel model;

    /** Data Access Object to handle user database operations */
    private UserDAO userDAO;


    /**
     * Constructs the UsersPanel, initializes the table, buttons, and
     * loads data from the database.
     */
    public UsersPanel() {
        userDAO = new UserDAO();
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
     * Loads all admin records from the database and displays them in the table.
     */
    private void loadData() {
        model.setRowCount(0); // Clear existing rows
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getPassword(),
                    u.getRole()
            });
        }
    }

    /**
     * Opens a modal dialog for adding a new user or editing an existing user.
     * The dialog contains form fields for name, email, password, and role.
     *
     * @param selectedRow The index of the user row to edit, or null to add a new user.
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
        formPanel.add(new JLabel("Full Name:")); formPanel.add(nameF);
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

            try { // -------- Validation section --------

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
     * Saves a new user or updates an existing user in the database.
     * Displays a confirmation dialog before performing the operation.
     *
     * @param id The user's ID (0 if creating a new user)
     * @param name Full name of the user
     * @param email Email address of the user
     * @param pass Password of the user
     * @param role Role of the user (e.g., "USER", "ADMIN")
     * @param isEdit True if updating an existing user, false if adding a new user
     */
    private void saveUser(int id, String name, String email, String pass, String role, boolean isEdit) {
        String confirmMsg = "Are you sure you want to " + (isEdit ? "update" : "add") + " this user?";
        if (JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        boolean success = userDAO.saveUser(id, name, email, pass, role, isEdit);

        if (success) {
            JOptionPane.showMessageDialog(this, "User " + (isEdit ? "updated" : "added") + " successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save user. Check the console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected user after user confirmation.
     * Prevents deletion if the user has booked tickets.
     * Updates the JTable after successful deletion.
     */
    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);

        if (userDAO.hasBookedTickets(userId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete this user. They have booked tickets.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user. Check the console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
