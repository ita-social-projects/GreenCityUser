package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to pass bad request.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
public class BadRequestException extends RuntimeException {
    /**
     * Base exception. returns code 400
     */
    public BadRequestException(String message) {
        super(message);
    }
}
