package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user by this email not found.
 */
@StandardException
public class WrongEmailException extends RuntimeException {
}