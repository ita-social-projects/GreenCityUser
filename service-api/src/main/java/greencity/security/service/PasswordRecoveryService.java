package greencity.security.service;

import greencity.security.dto.ownsecurity.OwnRestoreDto;

/**
 * Service for password recovery functionality. It manages recovery tokens
 * creation and persistence as well as minimal validation, but do neither
 * updates the password directly, nor sends a recovery email. These parts of the
 * recovery process are done by separate event listeners.
 *
 * @author Dmytro Dovhal && Yurii Koval.
 * @version 2.0
 */
public interface PasswordRecoveryService {
    /**
     * Sends an email with password restoration token.
     *
     * @param email - destination email address
     */
    void sendPasswordRecoveryEmailTo(String email, boolean isUbs, String language);

    /**
     * Restore password by token.
     *
     * @param form token that confirm that this user are owner of this email +
     *             password and confirm password.
     */
    void updatePasswordUsingToken(OwnRestoreDto form);
}
