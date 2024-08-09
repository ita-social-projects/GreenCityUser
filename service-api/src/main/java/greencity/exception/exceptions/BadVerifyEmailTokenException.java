package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 */
@StandardException
public class BadVerifyEmailTokenException extends RuntimeException {
}
