// Package declaration
package cinema.exception;

/**
 * InvalidInputException is a custom checked exception used for signaling
 * that user input failed validation checks.
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
