package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.position.PositionAuthoritiesDto;
import greencity.entity.Authority;
import greencity.entity.Position;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PositionServiceImpl implements PositionService {
    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;

    @Override
    public PositionAuthoritiesDto getPositionsAndRelatedAuthorities(String email) {
        var user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return PositionAuthoritiesDto.builder()
            .positionId(user.getPositions().stream().map(Position::getId).collect(Collectors.toList()))
            .authorities(user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.toList()))
            .build();
    }

    @Override
    public List<String> getEmployeeLoginPositionNames(String email) {
        String loginEmail = getEmployeeLogin();

        if (!email.equals(loginEmail)) {
            throw new BadRequestException(ErrorMessage.USER_DOES_NOT_LOGIN + email);
        }

        User employeeLogin = userRepo.findByEmail(loginEmail)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + loginEmail));

        return employeeLogin.getPositions()
            .stream()
            .map(Position::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getEmployeeLoginPositionNames() {
        String loginEmail = getEmployeeLogin();

        User employeeLogin = userRepo.findByEmail(loginEmail)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + loginEmail));

        return employeeLogin.getPositions()
            .stream()
            .map(Position::getName).collect(Collectors.toList());
    }

    private String getEmployeeLogin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
