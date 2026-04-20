// Package declaration
package cinema.dao;
// Import project-specific classes
import cinema.database.DatabaseConnection;
import cinema.models.User;
// Import standard Java libraries for SQL operations
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations related to users.
 * Includes login, registration, password management, email existence checks
 * and CRUD operations for UsersPanel.
 */
public class UserDAO {

    /**
     * LOGIN
     * Verifies user credentials and returns a User object if successful.
     *
     * @param email User's email
     * @param password User's password
     * @return User object if credentials match, null otherwise
     */
    public User loginUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log database errors internally
        }
        return null;
    }

    /**
     * REGISTER
     * Adds a new user to the database with role "USER".
     *
     * @param email User's email
     * @param password User's password
     * @param name User's name
     * @return true if insertion succeeds, false otherwise
     */
    public boolean registerUser(String email, String password, String name) {
        String query = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            pst.setString(2, password);
            pst.setString(3, name);
            pst.setString(4, "USER");

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CHANGE PASSWORD
     * Updates user's password after verifying the old password.
     *
     * @param email User's email
     * @param oldPass Current password
     * @param newPass New password
     * @return true if password updated successfully, false otherwise
     */
    public boolean changePassword(String email, String oldPass, String newPass) {
        String verifyQuery = "SELECT password FROM users WHERE email = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {

            verifyStmt.setString(1, email);
            ResultSet rs = verifyStmt.executeQuery();

            // Verify old password matches
            if (rs.next() && rs.getString("password").equals(oldPass)) {

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newPass);
                    updateStmt.setString(2, email);
                    updateStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log errors internally
        }
        return false;
    }

    /**
     * Checks if an email already exists in the users table.
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            return rs.next(); // true if email is found

        } catch (SQLException e) {
            e.printStackTrace(); // Log errors internally
        }
        return false;
    }


    //DAO for User Panel
    /**
     * Retrieves all users from the 'users' table.
     *
     * @return A List of User objects representing all users in the database.
     *         Returns an empty list if there are no users or if an error occurs.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Inserts a new user into the database.
     *
     * @return true if insertion was successful, false otherwise
     */
    public boolean addUser(String name, String email, String pass, String role) {

        // SQL insert query
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);

            pst.executeUpdate(); // Execute insert
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(int id, String name, String email, String pass, String role) {

        // SQL update query
        String sql = "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);
            pst.setInt(5, id);

            pst.executeUpdate(); // Execute update
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param userId The unique ID of the user to delete
     * @return True if deletion was successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL errors
            return false;
        }
    }

    /**
     * Checks if a user has any booked tickets in the system.
     *
     * @param userId The ID of the user to check
     * @return True if the user has one or more booked tickets, false otherwise
     */
    public boolean hasBookedTickets(int userId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND status = 'BOOKED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL errors
        }
        return false;
    }
}
