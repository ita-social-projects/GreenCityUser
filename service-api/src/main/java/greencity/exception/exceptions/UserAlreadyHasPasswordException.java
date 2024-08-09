package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that that is thrown when trying to set password for user that
 * already has one.
 *
 * @author Andrii Yezenitskyi.
 */
@StandardException
public class UserAlreadyHasPasswordException extends RuntimeException {
}
