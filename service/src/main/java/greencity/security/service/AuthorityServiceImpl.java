package greencity.security.service;

import greencity.constant.AppConstant;
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
    private final PositionService positionService;

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

        checkIfEmployeeLoginCanEditAuthorities(employee.getPositions()
            .stream()
            .map(Position::getName)
            .collect(Collectors.toList()));

        List<Authority> authorities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getAuthorities())) {
            authorities = authorityRepo.findAuthoritiesByNames(dto.getAuthorities());
        }
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    @Override
    public void updateAuthoritiesToRelatedPositions(EmployeePositionsDto dto) {
        User employee = userRepo.findByEmail(dto.getEmail()).orElseThrow(
            () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail()));

        checkIfEmployeeLoginCanEditAuthorities(dto.getPositions()
            .stream()
            .map(PositionDto::getName)
            .collect(Collectors.toList()));

        List<String> positionNames = dto.getPositions().stream()
            .map(PositionDto::getName).collect(Collectors.toList());

        List<Position> positions = positionRepo.findPositionsByNames(positionNames);
        List<Authority> list = authorityRepo.findAuthoritiesByPositions(positionNames);

        employee.setPositions(positions);
        employee.setAuthorities(list);
        userRepo.save(employee);
    }

    private void checkIfEmployeeLoginCanEditAuthorities(List<String> employeePositionNames) {
        var employeeLoginPositionNames = positionService.getEmployeeLoginPositionNames();

        if (!employeeLoginPositionNames.contains(AppConstant.EMPLOYEE_SUPER_ADMIN)
            && employeePositionNames.contains(AppConstant.EMPLOYEE_SUPER_ADMIN)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        if (!employeeLoginPositionNames.contains(AppConstant.EMPLOYEE_SUPER_ADMIN)
            && employeeLoginPositionNames.contains(AppConstant.EMPLOYEE_ADMIN)
            && employeePositionNames.contains(AppConstant.EMPLOYEE_ADMIN)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        if (!employeeLoginPositionNames.contains(AppConstant.EMPLOYEE_SUPER_ADMIN)
            && !employeeLoginPositionNames.contains(AppConstant.EMPLOYEE_ADMIN)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }
}
