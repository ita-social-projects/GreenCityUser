package greencity.exception.exceptions;

/**
 * Exception that user status is wrong for login.
 *
 * @author Volodymyr Kharchenko
 * @version 1.0
 */
public class BadUserStatusException extends RuntimeException {
    /**
     * Constructor for BadUserStatusException.
     *
     * @param message - giving message.
     */
    public BadUserStatusException(String message) {
        super(message);
    }
}
