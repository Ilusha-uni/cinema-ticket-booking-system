// Package declaration
package cinema.exception;

/**
 * Represents a custom checked exception thrown when user input fails validation.
 * Thrown by the InputValidator utility class when an input does not meet
 * the expected format, type, or constraints.
 */
public class InvalidInputException extends Exception {

    /**
     * Constructs a new InvalidInputException with the specified detail message.
     *
     * @param message the detail message explaining why the input was invalid
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
