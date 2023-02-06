package greencity.exception.exceptions;

/**
 * Exception that user status is wrong.
 */
public class BadUserStatusException extends RuntimeException{
    /**
     * Constructor.
     */
    public BadUserStatusException(String message) {
        super(message);
    }
}
