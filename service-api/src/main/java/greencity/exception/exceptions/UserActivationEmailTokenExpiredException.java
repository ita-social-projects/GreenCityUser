package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user trying to verify email with token that has
 * expired.
 *
 * @author Nazar Stasyuk
 */
@StandardException
public class UserActivationEmailTokenExpiredException extends RuntimeException {
}
