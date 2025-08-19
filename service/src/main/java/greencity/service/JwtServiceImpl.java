package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.UserRepo;
import greencity.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    public Long findUserIdByEmail(String email) {
        return userRepo.findIdByEmail(email).orElseThrow(
            () -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
    }
}
