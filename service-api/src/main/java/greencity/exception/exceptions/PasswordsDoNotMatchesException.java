package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user whan passwords don't matches.
 *
 * @author Dmytro Dovhal
 * @version 1.0
 */
@StandardException
public class PasswordsDoNotMatchesException extends RuntimeException {
}
