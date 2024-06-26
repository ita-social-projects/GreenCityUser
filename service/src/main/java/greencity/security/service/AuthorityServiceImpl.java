package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.position.PositionDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.Authority;
import greencity.entity.Position;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import greencity.service.EmailService;
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
    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;
    private final PositionRepo positionRepo;
    private final EmailService emailService;

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
        if (validateOnlyDriverPosition(employee)) {
            sendRestorePasswordEmail(employee);
        }
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    private boolean validateOnlyDriverPosition(User employee) {
        List<Position> employeePositions = employee.getPositions();
        return employee.getAuthorities().isEmpty()
            && employee.getRestorePasswordEmail() != null
            && employeePositions.size() == 1
            && employeePositions.stream()
                .map(Position::getNameEn)
                .anyMatch("Driver"::equals);
    }

    private void sendRestorePasswordEmail(User employee) {
        emailService.sendRestoreEmail(
            employee.getId(),
            employee.getFirstName(),
            employee.getEmail(),
            employee.getRestorePasswordEmail().getToken(),
            employee.getLanguage().getCode(),
            checkRole(employee.getRole()));
    }

    private boolean checkRole(Role role) {
        return role.equals(Role.ROLE_UBS_EMPLOYEE);
    }

    @Override
    public void updateAuthoritiesToRelatedPositions(EmployeePositionsDto dto) {
        User employee = userRepo.findByEmail(dto.getEmail()).orElseThrow(
            () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail()));

        List<String> positionNames = dto.getPositions().stream()
            .map(PositionDto::getName).collect(Collectors.toList());

        List<Position> positions = positionRepo.findPositionsByNames(positionNames);
        List<Authority> list = authorityRepo.findAuthoritiesByPositions(positionNames);

        if (validateOnlyDriverPosition(employee)) {
            sendRestorePasswordEmail(employee);
        }

        employee.setPositions(positions);
        employee.setAuthorities(list);
        userRepo.save(employee);
    }
}
