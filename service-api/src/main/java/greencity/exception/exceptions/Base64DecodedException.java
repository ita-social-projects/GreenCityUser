package greencity.exception.exceptions;

/**
 * Exception that we get when image in base64 can't decode.
 */
public class Base64DecodedException extends RuntimeException {
    public Base64DecodedException(String message) {
        super(message);
    }
}
