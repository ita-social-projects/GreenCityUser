package greencity.exception.exceptions;

public class NotValidBooleanValueException extends RuntimeException {
    /**
     * Exception we get when we receive wrong data for boolean fields.
     */
    public NotValidBooleanValueException(String message) {
        super(message);
    }
}
