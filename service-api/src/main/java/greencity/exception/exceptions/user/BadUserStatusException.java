package greencity.exception.exceptions.user;

import greencity.exception.exceptions.validation.BadRequestException;

/**
 * Exception that user status is wrong for login.
 *
 * @author Volodymyr Kharchenko
 * @version 1.0
 */
public class BadUserStatusException extends BadRequestException {
    /**
     * Exception we get when we receive wrong user status. returns code 400
     */
    public BadUserStatusException(String message) {
        super(message);
    }
}
