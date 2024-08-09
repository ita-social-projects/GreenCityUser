package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that user status is wrong for login.
 *
 * @author Volodymyr Kharchenko
 * @version 1.0
 */
@StandardException
public class BadUserStatusException extends RuntimeException {
}
