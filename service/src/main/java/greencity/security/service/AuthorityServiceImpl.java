package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.Authority;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityRepo authorityRepo;
    private final UserRepo userRepo;

    @Override
    public Set<String> getAllEmployeesAuthorities(String email) {
        User user =
            userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return authorityRepo.getAuthoritiesByEmployeeId(user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEmployeesAuthorities(UserEmployeeAuthorityDto dto, String email) {
        User user =
            userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        User employee =
            userRepo.findByEmail(dto.getEmployeeEmail())
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmployeeEmail()));
        if (!user.getRole().equals(Role.ROLE_UBS_EMPLOYEE) || employee.getRole().equals(Role.ROLE_USER)
            || user.getEmail().equals(employee.getEmail())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        deleteOldAuthorities(employee);
        saveNewAuthorities(dto, employee);
    }

    private void deleteOldAuthorities(User employee) {
        for (Authority authority : employee.getAuthorities()) {
            List<User> users = authority.getEmployees();
            users.removeIf(u -> u.equals(employee));
        }
    }

    private void saveNewAuthorities(UserEmployeeAuthorityDto dto, User employee) {
        for (String name : dto.getAuthorities()) {
            Authority authority = authorityRepo.findByName(name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_AUTHORITY + name));
            List<User> users = authority.getEmployees();
            users.add(employee);
            authority.setEmployees(users);
            authorityRepo.save(authority);
        }
    }
}
