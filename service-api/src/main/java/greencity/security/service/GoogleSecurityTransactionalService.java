package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import greencity.entity.User;

public interface GoogleSecurityTransactionalService {
    /**
     * Method that allows to create and save a user with payload of google token.
     *
     * @param payload  {@link GoogleIdToken.Payload} - payload google id token.
     * @param language {@link String} - language.
     * @return {@link User} - registered user.
     */
    User signUp(GoogleIdToken.Payload payload, String language);
}
