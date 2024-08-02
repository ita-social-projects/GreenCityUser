package greencity.exception.exceptions.resource;

import greencity.exception.exceptions.validation.BadRequestException;

/**
 * Exception that we get when admin/moderator trying to update himself.
 *
 * @author Rostyslav Khasanov
 */
public class BadUpdateRequestException extends BadRequestException {
    /**
     * Exception we get when we receive wrong update request. returns code 400
     */
    public BadUpdateRequestException(String message) {
        super(message);
    }
}
