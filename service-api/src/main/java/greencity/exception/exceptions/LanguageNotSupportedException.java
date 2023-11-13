package greencity.exception.exceptions;

/**
 * Exception, that is throw after saving object with language code, that doesn't
 * exist in database.
 */
public class LanguageNotSupportedException extends RuntimeException {
    /**
     * Default constructor.
     */
    public LanguageNotSupportedException() {
    }

    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public LanguageNotSupportedException(String message) {
        super(message);
    }
}
