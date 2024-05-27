package greencity.exception.exceptions;

/**
 * Exception that we get when ID token (JWT) is not valid or expired.
 *
 * @author Anton Bondar
 */
public class IdTokenExpiredException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public IdTokenExpiredException(String message) {
        super(message);
    }
}
