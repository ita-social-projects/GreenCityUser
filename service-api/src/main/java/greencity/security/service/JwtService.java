package greencity.security.service;

import greencity.dto.user.UserVO;

public interface JwtService {
    /**
     * Find UserVO's id by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} id
     */
    Long findUserIdByEmail(String email);
}
