package greencity.exception.exceptions.resource;

public class NotSavedException extends RuntimeException {
    /**
     * Constructor for NotSavedException.
     *
     * @param message - giving message.
     */
    public NotSavedException(String message) {
        super(message);
    }
}
