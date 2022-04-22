package greencity.exception.exceptions;

/**
 * Exception that that is thrown when trying to set password for user that
 * already has one.
 *
 * @author Andrii Yezenitskyi.
 */
public class UserAlreadyHasPasswordException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message {@link String} cause of the exception.
     */
    public UserAlreadyHasPasswordException(String message) {
        super(message);
    }
}
