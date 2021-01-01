package greencity.exception.exceptions;

/**
 * Should be thrown when a use tries to sign in before email verification.
 */
public class EmailNotVerified extends RuntimeException {
    /**
     * Constructor.
     */
    public EmailNotVerified(String message) {
        super(message);
    }
}
