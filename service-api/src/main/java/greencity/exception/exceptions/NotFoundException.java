package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when we send request(for e.x. to findById) and there is
 * no record with this id, then we get {@link NotFoundException}.
 */
@StandardException
public class NotFoundException extends RuntimeException {
}