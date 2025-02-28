package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;

/**
 * Provides the Google social logic.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */

public interface GoogleSecurityService {
    /**
     * Method that allow you to authenticate with Google token.
     *
     * @param token {@link String} - Google id token.
     * @return {@link SuccessSignInDto} if token valid
     */
    SuccessSignInDto authenticate(String token, String language);
}
