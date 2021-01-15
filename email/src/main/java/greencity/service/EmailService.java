package greencity.service;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */
public interface EmailService {
    /**
     * Method for sending verification email to {@link User}.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token current user.
     */
    void sendVerificationEmail(Long userId, String userName, String userEmail, String token, String language);

    /**
     * Method for sending user approval email to User, when Admin adds the User from
     * admin panel.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token.
     */
    void sendApprovalEmail(Long userId, String userName, String userEmail, String token);

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId       the user id is used for recovery link building.
     * @param userFistName user first name is used in email model constants.
     * @param userEmail    user email which will be used for sending recovery
     *                     letter.
     * @param token        password recovery token.
     */
    void sendRestoreEmail(Long userId, String userFistName, String userEmail, String token, String language);
}
