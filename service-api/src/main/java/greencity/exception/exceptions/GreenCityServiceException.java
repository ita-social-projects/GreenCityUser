package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that can be thrown when call GreenCity app using WebClient but
 * GreenCity app returns a 500 error.
 */
@StandardException
public class GreenCityServiceException extends RuntimeException {
}
