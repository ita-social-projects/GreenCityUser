package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to refresh access token with bad
 * refresh token.
 *
 * @author Nazar Stasyuk
 */
public class BadRefreshTokenException extends BadRequestException {
    /**
     * Exception we get when we receive wrong refresh token. returns code 400
     */
    public BadRefreshTokenException(String message) {
        super(message);
    }
}
