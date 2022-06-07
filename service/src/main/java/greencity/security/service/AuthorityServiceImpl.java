package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService{

    private final AuthorityRepo authorityRepo;
    private final UserRepo userRepo;

    @Override
    public Set<String> getAllEmploeesAuthorities(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return authorityRepo.getAuthoritiesByEmployeeId(user.getId());
    }

}
