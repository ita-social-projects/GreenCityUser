package greencity.security.service;

/**
 * Service that does email verification.
 */
public interface VerifyEmailService {
    /**
     * Verifies email by token.
     *
     * @param userId {@link Long} - user's id.
     * @param token  {@link String} - token that confirms the user is the owner of
     *               email.
     */
    Boolean verifyByToken(Long userId, String token);

    /**
     * This method remove all accounts that not verified by email at 00:00.
     */
    void removeUnusedTokensWithAccounts();
}
