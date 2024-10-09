package greencity.exception.exceptions;

/**
 * Exception that we get when token expired.
 */
public class IdTokenExpiredException extends RuntimeException {
    /**
     * Exception that we get when token expired.
     */
    public IdTokenExpiredException(String message) {
        super(message);
    }
}
