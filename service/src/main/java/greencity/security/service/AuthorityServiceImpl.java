package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

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
            userRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + dto.getEmployeeId()));
        if (!user.getRole().equals(Role.ROLE_ADMIN) || employee.getRole().equals(Role.ROLE_USER)
            || user.getEmail().equals(employee.getEmail())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        List<Authority> authorities = employee.getAuthorities();
        for (String name : dto.getAuthorities()) {
            Authority authority = authorityRepo.findByName(name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_AUTHORITY + name));
            if (Boolean.TRUE.equals(checkAuthoritiesEmployee(authorities, authority))) {
                authorities.add(authority);
                List<User> users = authority.getEmployees();
                users.add(employee);
                authority.setEmployees(users);
                authorityRepo.save(authority);
            }
        }
    }

    /**
     * The method checks whether the employee already has this authority.
     *
     * @param authority   new authority.
     * @param authorities employee's authority list.
     * @author Hlazova Nataliia
     */
    private Boolean checkAuthoritiesEmployee(List<Authority> authorities, Authority authority) {
        for (Authority atr : authorities) {
            if (atr.equals(authority)) {
                return false;
            }
        }
        return true;
    }
}
