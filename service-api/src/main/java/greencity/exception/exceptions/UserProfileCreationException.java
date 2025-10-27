package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception thrown when user profile in external service wasn't created.
 *
 * @author Rostyslav Zadyraichuk
 */
@StandardException
public class UserProfileCreationException extends RuntimeException {
}
