package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user trying to sign-up with email that already
 * registered.
 *
 * @author Nazar Stasyuk
 */
@StandardException
public class UserAlreadyRegisteredException extends RuntimeException {
}
