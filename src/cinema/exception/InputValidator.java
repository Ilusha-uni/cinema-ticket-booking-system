package cinema.exception;
import java.util.Date;

import cinema.database.DatabaseConnection;

import java.sql.*;

/**
 * InputValidator utility class.
 * Note: Changed from 'extends Exception' to a standard class,
 * as it contains static utility methods rather than being an exception itself.
 */
public class InputValidator{

    // Validate if a string can be parsed as a positive integer
    public static void validatePositiveInteger(String input) throws InvalidInputException {
        try {
            int value = Integer.parseInt(input);
            if (value <= 0) {
                throw new InvalidInputException("Value must be a positive integer.");
            }
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Invalid input. Please enter a valid positive integer.");
        }
    }

    // Validate if a string can be parsed as a rating between 0 and 10
    public static void validateRating(String input) throws InvalidInputException {
        try {
            double rating = Double.parseDouble(input);
            if (rating < 0 || rating > 10) {
                throw new InvalidInputException("Rating must be between 0 and 10.");
            }
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Invalid rating format. Please enter a valid number between 0 and 10.");
        }
    }

    // Validate email format (using regex) and check if email is already taken
    public static void validateEmail(String email, int userId) throws InvalidInputException {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email == null || !email.matches(emailRegex)) {
            throw new InvalidInputException("Invalid email format. Please enter a valid email address.");
        }

        // Check if the email already exists in the database, excluding the current user's email
        if (isEmailTaken(email, userId)) {
            throw new InvalidInputException("This email is already registered.");
        }
    }

    // Method to check if the email already exists in the database
    private static boolean isEmailTaken(String email, int userId) throws InvalidInputException {
        // Assumes DatabaseConnection class exists in your project
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setInt(2, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new InvalidInputException("Database error: " + ex.getMessage());
        }
        return false;
    }

    // Validate if the field is not empty
    public static void validateNonEmpty(String input, String fieldName) throws InvalidInputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    // Validate Full Name (must only contain letters and spaces)
    public static void validateFullName(String fullName) throws InvalidInputException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidInputException("Full Name cannot be empty.");
        }

        String nameRegex = "^[a-zA-Z\\s]+$";
        if (!fullName.matches(nameRegex)) {
            throw new InvalidInputException("Full Name can only contain letters and spaces.");
        }
    }

    // Validate if a release date is valid
    public static void validateReleaseDate(Date releaseDate) throws InvalidInputException {
        if (releaseDate == null) {
            throw new InvalidInputException("Please select a valid release date.");
        }
    }

    // Method to validate role (must be either "ADMIN" or "USER")
    public static void validateRole(String role) throws InvalidInputException {
        if (role == null || !(role.equals("ADMIN") || role.equals("USER"))) {
            throw new InvalidInputException("Role must be either ADMIN or USER.");
        }
    }
}
