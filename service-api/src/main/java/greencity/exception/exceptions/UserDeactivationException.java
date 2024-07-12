package greencity.exception.exceptions;

/**
 * Exception thrown when there is an issue with deactivating a user.
 *
 * @author Kizerov Dmytro
 */
public class UserDeactivationException extends RuntimeException {
    public UserDeactivationException(String message) {
        super(message);
    }
}
