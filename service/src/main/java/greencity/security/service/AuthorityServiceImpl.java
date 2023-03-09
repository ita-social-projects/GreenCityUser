package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.UpdateEmployeeAuthoritiesDto;
import greencity.dto.position.PositionDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.Authority;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void updateEmployeesAuthorities(UserEmployeeAuthorityDto dto) {
        User employee = userRepo.findByEmail(dto.getEmployeeEmail())
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmployeeEmail()));
        if (!employee.getRole().equals(Role.ROLE_UBS_EMPLOYEE)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        List<Authority> authorities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getAuthorities())) {
            authorities = authorityRepo.findAuthoritiesByNames(dto.getAuthorities());
        }
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    @Override
    public void updateAuthorities(UpdateEmployeeAuthoritiesDto dto) {
        User employee = userRepo.findByEmail(dto.getEmail()).orElseThrow(
            () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail()));
        List<String> positionNames = dto.getPositions().stream()
            .map(PositionDto::getName).collect(Collectors.toList());
        List<Authority> list = authorityRepo.findAuthoritiesByPositions(positionNames);
        employee.setAuthorities(list);
        userRepo.save(employee);
    }
}
