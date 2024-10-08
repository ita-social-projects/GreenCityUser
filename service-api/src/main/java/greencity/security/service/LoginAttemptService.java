package greencity.security.service;

/**
 * Service to control brute-force attacks.
 *
 * @author Kizerov Dmytro
 * @version 1.0
 */
public interface LoginAttemptService {
    /**
     * This method is called when login failed.
     *
     * @param key identifies user, usually IP address.
     */
    void loginFailed(String key);

    /**
     * Checks if user is blocked.
     *
     * @return true if user is blocked, false otherwise.
     */
    boolean isBlocked();
}
