package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when ID token (JWT) is not valid or expired.
 *
 * @author Anton Bondar
 */
@StandardException
public class IdTokenExpiredException extends RuntimeException {
}
