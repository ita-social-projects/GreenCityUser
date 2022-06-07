package greencity.security.service;

import java.util.Set;

public interface AuthorityService {
    /**
     * Method gets all Employee authorities.
     *
     * @return Set of {@link String}.
     */
    Set<String> getAllEmploeesAuthorities(String email);
}
