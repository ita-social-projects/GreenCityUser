package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user trying to refresh access token with bad
 * refresh token.
 *
 * @author Nazar Stasyuk
 */
@StandardException
public class BadRefreshTokenException extends RuntimeException {
}
