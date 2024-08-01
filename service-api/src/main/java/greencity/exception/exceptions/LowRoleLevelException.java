package greencity.exception.exceptions;

import org.springframework.security.access.AccessDeniedException;

/**
 * Exception that we get when moderator trying to update user status of admin or
 * another moderator.
 *
 * @author Rostyslav Khasanov
 */
public class LowRoleLevelException extends AccessDeniedException {
    /**
     * Constructor for LowRoleLevelException.
     *
     * @param message - giving message.
     */
    public LowRoleLevelException(String message) {
        super(message);
    }
}
