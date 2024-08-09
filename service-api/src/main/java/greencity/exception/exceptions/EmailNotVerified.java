package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Should be thrown when a use tries to sign in before email verification.
 */
@StandardException
public class EmailNotVerified extends RuntimeException {
}
