package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 */
public class BadVerifyEmailTokenException extends BadRequestException {
    /**
     * Exception we get when we receive wrong verify email token. returns code 400
     */
    public BadVerifyEmailTokenException(String message) {
        super(message);
    }
}
