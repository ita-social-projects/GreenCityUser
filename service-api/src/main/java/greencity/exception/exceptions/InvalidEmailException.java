package greencity.exception.exceptions;

/**
 * Exception, that is throw when EmailAddress isn't valid.
 */
public class InvalidEmailException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}