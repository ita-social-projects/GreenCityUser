package greencity.security.service;

/**
 * Service to control brute-force attacks.
 *
 * @author Kizerov Dmytro
 * @version 1.0
 */
public interface LoginAttemptService {
    /**
     * Method to increment the amount of wrong captcha for the given {@code email}.
     * This method is called when a user provides wrong captcha.
     *
     * @param email identifies the user.
     *
     */
    void loginFailedByCaptcha(String email);

    /**
     * Method to check if user is blocked by wrong captcha.
     *
     * @param email identifies user, usually email.
     * @return true if user is blocked, false otherwise.
     */
    boolean isBlockedByCaptcha(String email);

    /**
     * Method to increment the amount of wrong password for the given {@code email}.
     * This method is called when a user provides wrong password.
     *
     * @param email identifies the user.
     */
    void loginFailedByWrongPassword(String email);

    /**
     * Method to check if user is blocked by wrong password.
     *
     * @param email identifies user, usually email.
     * @return true if user is blocked, false otherwise.
     */
    boolean isBlockedByWrongPassword(String email);

    /**
     * Deletes the given {@code email} from cache.
     *
     * @param email identifies the user.
     */
    void deleteEmailFromCache(String email);
}
