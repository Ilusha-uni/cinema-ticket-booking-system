// Package declaration
package cinema.exception;

// Import necessary libraries for database connection (if needed) and Date handling
import cinema.database.DatabaseConnection;
import java.util.Date;
import java.sql.*;

/**
 * InputValidator is a utility class that provides static methods to validate
 * user input for fields such as email, full name, role, numeric values, and dates.
 * Throws InvalidInputException if validation fails.
 */
public class InputValidator {

    /**
     * Validates an email address.
     * Checks format using regex.
     *
     * @param email the email string to validate
     * @param userId the user ID (optional, could be used to check uniqueness in DB)
     * @throws InvalidInputException if email is null, empty, or invalid format
     */
    public static void validateEmail(String email, int userId) throws InvalidInputException {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email == null || !email.matches(emailRegex)) {
            throw new InvalidInputException("Invalid email format. Please enter a valid email address.");
        }
    }

    /**
     * Validates that a field is not empty.
     *
     * @param input the string input to check
     * @param fieldName the name of the field (used in error message)
     * @throws InvalidInputException if input is null or blank
     */
    public static void validateNonEmpty(String input, String fieldName) throws InvalidInputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates a full name (letters and spaces only).
     *
     * @param fullName the name string to validate
     * @throws InvalidInputException if name is empty or contains invalid characters
     */
    public static void validateFullName(String fullName) throws InvalidInputException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidInputException("Full Name cannot be empty.");
        }

        String nameRegex = "^[a-zA-Z\\s]+$";
        if (!fullName.matches(nameRegex)) {
            throw new InvalidInputException("Full Name can only contain letters and spaces.");
        }
    }

    /**
     * Validates that a release date is not null.
     *
     * @param releaseDate the Date object to validate
     * @throws InvalidInputException if releaseDate is null
     */
    public static void validateReleaseDate(Date releaseDate) throws InvalidInputException {
        if (releaseDate == null) {
            throw new InvalidInputException("Please select a valid release date.");
        }
    }

    /**
     * Validates a role value. Must be "ADMIN" or "USER".
     *
     * @param role the role string to validate
     * @throws InvalidInputException if role is null or invalid
     */
    public static void validateRole(String role) throws InvalidInputException {
        if (role == null || !(role.equals("ADMIN") || role.equals("USER"))) {
            throw new InvalidInputException("Role must be either ADMIN or USER.");
        }
    }

    /**
     * Validates if a string is a positive integer.
     *
     * @param input the string to parse
     * @throws InvalidInputException if parsing fails or number is <= 0
     */
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

    /**
     * Validates a numeric rating between 0 and 10.
     *
     * @param input the rating string to validate
     * @throws InvalidInputException if parsing fails or number is out of range
     */
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
}
