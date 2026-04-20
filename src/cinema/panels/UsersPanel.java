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
 * Provides a Swing-based interface for managing user records in the cinema system.
 * It provides CRUD functionality:
 * - Create: Add new users
 * - Read: Display all users in a table
 * - Update: Edit existing users
 * - Delete: Remove users (with checks for booked tickets)
 *
 * This panel communicates with the database through the AdminDAO class
 * and validates user input using the InputValidator utility.
 */
public class UsersPanel extends JPanel {
    /**
     * JTable to display the list of users. This table will dynamically update based on database data.
     */
    private JTable table;

    /**
     * JPanel that holds the buttons for user management operations (Refresh, Add, Edit, Delete).
     */
    private JPanel buttonPanel;

    /**
     * Table model to manage the table data dynamically, with columns like id, name, email, password, and role.
     */
    private DefaultTableModel model;

    /**
     * Data Access Object to handle user-related database operations, such as adding, updating, or deleting users.
     */
    private UserDAO userDAO;


    /**
     * Constructs the UsersPanel, initializes the table, buttons, and loads user data from the database.
     * This is the main constructor that sets up the UI and prepares the data model.
     */
    public UsersPanel() {
        // Data Initialization: Creates an instance of UserDAO to interact with the database
        userDAO = new UserDAO();

        // UI Setup: Initializes the table and buttons, sets up layout
        initComponents();
        layoutComponents();

        // Initial Data Load: Loads users from the database to populate the table
        loadData();
    }

    /**
     * Initializes the table model and the buttons, and sets up action listeners for the buttons.
     * This method separates the UI initialization logic for clarity and maintainability.
     */
    private void initComponents() {
        // Initialize table model with column headers and empty data
        String[] columns = {"id", "name", "email", "password", "role"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Initialize Buttons and Listeners
        setupButtons();
    }

    /**
     * Sets up the CRUD (Create, Read, Update, Delete) buttons and their respective action listeners.
     * This method configures each button with a specific action to be performed when clicked.
     */
    private void setupButtons() {
        // Create CRUD action buttons for user management
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action listener for refreshing the user list from the database
        refreshBtn.addActionListener(e -> loadData());

        // Action listener for adding a new user (opens a dialog to input user details)
        addBtn.addActionListener(e -> showAddUserDialog());

        // Action listener for editing an existing user (opens a dialog with pre-filled user data)
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) showEditUserDialog(row);
            else JOptionPane.showMessageDialog(this, "Select a row to edit");
        });

        // Action listener for deleting a selected user (with validation checks)
        deleteBtn.addActionListener(e -> handleDeleteUser());

        // Add buttons to the button panel for easy layout management
        buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
    }

    /**
     * Layouts the components of the UsersPanel using BorderLayout.
     * Adds the JTable (inside a scroll pane) to the center and the button panel to the bottom.
     */
    private void layoutComponents() {
        // Set layout to BorderLayout: table in center, buttons at bottom
        setLayout(new BorderLayout());
        // Add the table (inside a scroll pane) and the button panel to the main panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    /**
     * Creates a modal dialog attached to this panel.
     *
     * @param title title of the dialog window
     * @return configured JDialog instance
     */
    private JDialog createDialog(String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        return dialog;
    }

    /**
     * Creates a form panel containing input fields for user data.
     *
     * @return JPanel containing labeled input fields
     */
    private JPanel createFormPanel(JTextField nameF, JTextField emailF,
                                   JPasswordField passF, JTextField roleF) {

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Full Name:")); panel.add(nameF);
        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Password:")); panel.add(passF);
        panel.add(new JLabel("Role:")); panel.add(roleF);

        return panel;
    }

    /**
     * Creates a panel containing action buttons (Save / Cancel).
     *
     * @return JPanel with buttons
     */
    private JPanel createButtonPanel(JButton saveBtn, JButton cancelBtn) {
        JPanel panel = new JPanel();
        panel.add(saveBtn);
        panel.add(cancelBtn);
        return panel;
    }

    /**
     * Displays a dialog centered on the parent panel.
     * The dialog is packed and then made visible.
     */
    private void showDialog(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Loads all user records from the database and refreshes Table view.
     * This method represents the READ operation in CRUD.
     * It clears existing table data and repopulates it with fresh data
     * from the database via UserDAO.
     */
    private void loadData() {
        // Clear table
        model.setRowCount(0);
        // Load users from DB
        List<User> users = userDAO.getAllUsers();
        // Populate table
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
     * Opens a dialog window for creating a new user.
     * This method prepares empty input fields and delegates
     * saving logic to handleAddUser().
     */
    private void showAddUserDialog() {
        // Create dialog
        JDialog dialog = createDialog("Add User");

        // Input fields
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField roleField = new JTextField(20);

        // Form panel
        JPanel formPanel = createFormPanel(nameField, emailField, passwordField, roleField);

        // Buttons
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        // Save action
        saveBtn.addActionListener(e ->
                handleAddUser(dialog, nameField, emailField, passwordField, roleField)
        );

        // Close dialog
        cancelBtn.addActionListener(e -> dialog.dispose());

        // Build dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(createButtonPanel(saveBtn, cancelBtn), BorderLayout.SOUTH);

        // Show dialog
        showDialog(dialog);
    }

    /**
     * Opens a dialog window for editing an existing user.
     * The selected row from JTable is used to pre-fill fields.
     * @param selectedRow index of selected table row
     */
    private void showEditUserDialog(int selectedRow) {
        JDialog dialog = createDialog("Edit User");

        JTextField nameField = new JTextField((String) model.getValueAt(selectedRow, 1), 20);
        JTextField emailField = new JTextField((String) model.getValueAt(selectedRow, 2), 20);
        JPasswordField passwordField = new JPasswordField((String) model.getValueAt(selectedRow, 3), 20);
        JTextField roleField = new JTextField((String) model.getValueAt(selectedRow, 4), 20);

        int userId = (int) model.getValueAt(selectedRow, 0);

        JPanel formPanel = createFormPanel(nameField, emailField, passwordField, roleField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e ->
                handleEditUser(dialog, userId, nameField, emailField, passwordField, roleField)
        );

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(createButtonPanel(saveBtn, cancelBtn), BorderLayout.SOUTH);

        showDialog(dialog);
    }

    /**
     * Handles creation of a new user after validation and confirmation.
     * This represents the CREATE operation in CRUD.
     * After successful insertion, the table is refreshed.
     */
    private void handleAddUser(JDialog dialog,
                               JTextField nameField,
                               JTextField emailField,
                               JPasswordField passwordField,
                               JTextField roleField) {

        // Get input values
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleField.getText().trim();

        try {
            // Validate input
            validateInput(name, email, password, role, 0);

            // Confirm action
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to add this user?",
                    "Confirm Add",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            // Save to Database
            boolean success = userDAO.addUser(name, email, password, role);

            // Success feedback and then refresh table
            if (success) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                dialog.dispose();
                loadData();
            }

        } catch (InvalidInputException ex) {
            // Validation error
            JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Handles updating an existing user.
     * This represents the UPDATE operation in CRUD.
     * Validates input, confirms action, updates DB and refreshes table.
     */
    private void handleEditUser(JDialog dialog, int userId,
                                JTextField nameField,
                                JTextField emailField,
                                JPasswordField passwordField,
                                JTextField roleField) {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleField.getText().trim();

        try {
            validateInput(name, email, password, role, userId);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to update this user?",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            // Save to DB
            boolean success = userDAO.updateUser(userId, name, email, password, role);

            // Success feedback and then refresh table
            if (success) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                dialog.dispose();
                loadData();
            }

        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes selected user from database.
     *
     * This represents DELETE operation in CRUD.
     * It also checks that user cannot be deleted if they have booked tickets.
     */
    private void handleDeleteUser() {
        // Get selected row
        int row = table.getSelectedRow();
        // No selection check
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
            return;
        }
        // Get user ID
        int userId = (int) model.getValueAt(row, 0);

        // Prevent deletion if user has booked tickets
        if (userDAO.hasBookedTickets(userId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete this user. They have booked tickets.");
            return;
        }

        // Confirm deletion
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // Delete from DB
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user. Check the console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Validates user input before database operations.
     *
     * @throws InvalidInputException if any validation rule fails
     */
    private void validateInput(String name, String email, String pass, String role, int userId)
            throws InvalidInputException {

        // Required fields
        InputValidator.validateNonEmpty(name, "Name");
        InputValidator.validateNonEmpty(email, "Email");
        InputValidator.validateNonEmpty(pass, "Password");

        // Password check
        if (pass.length() < 6)
            throw new InvalidInputException("Password must be at least 6 characters long.");

        // Validation
        InputValidator.validateNonEmpty(role, "Role");
        InputValidator.validateEmail(email, userId);
        InputValidator.validateFullName(name);
        InputValidator.validateRole(role);
    }

}
