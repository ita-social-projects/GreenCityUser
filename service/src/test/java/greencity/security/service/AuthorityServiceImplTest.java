package greencity.security.service;

import static greencity.ModelUtils.TEST_EMAIL;
import static greencity.ModelUtils.createAdmin;
import static greencity.ModelUtils.createEmployee;
import static greencity.ModelUtils.getAuthority;
import static greencity.ModelUtils.getPositions;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserEmployeeAuthorityDto;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.position.PositionDto;
import greencity.entity.Authority;
import greencity.entity.Position;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorityServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthorityRepo authorityRepo;
    @Mock
    private PositionRepo positionRepo;
    @Mock
    private PositionService positionService;
    @Mock
    private Authentication auth;
    @InjectMocks
    private AuthorityServiceImpl authorityService;

    @BeforeEach
    public void initSecurityContext() {
        when(auth.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllEmployeesAuthoritiesTest() {
        Set<String> expected = new HashSet<>();
        expected.add("test");
        expected.add("test1");
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(getUser()));
        when(authorityRepo.getAuthoritiesByEmployeeId(getUser().getId())).thenReturn(expected);
        assertEquals(expected, authorityService.getAllEmployeesAuthorities(TEST_EMAIL));

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(authorityRepo).getAuthoritiesByEmployeeId(getUser().getId());
    }

    @Test
    void updateEmployeesAuthoritiesTest() {
        User employee = createEmployee();
        List<Authority> authority = List.of(getAuthority());
        List<String> authoritiesName = authority.stream().map(Authority::getName)
            .collect(Collectors.toList());

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        when(authorityRepo.findAuthoritiesByNames(authoritiesName)).thenReturn(authority);
        when(positionService.getEmployeeLoginPositionNames()).thenReturn(List.of("Супер адмін"));

        employee.setAuthorities(authority);
        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto());

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(authorityRepo).findAuthoritiesByNames(authoritiesName);
        verify(userRepo).save(employee);
    }

    @Test
    void updateEmployeesAuthoritiesThrowsNotFoundExceptionTest() {
        var dto = getUserEmployeeAuthorityDto();
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> authorityService.updateEmployeesAuthorities(dto));
        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void updateEmployeesAuthoritiesThrowsBadRequestExceptionTest() {
        User employee = createEmployee();
        employee.setRole(Role.ROLE_USER);

        var dto = getUserEmployeeAuthorityDto();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));

        assertEquals(Role.ROLE_USER, employee.getRole());
        assertThrows(BadRequestException.class,
            () -> authorityService.updateEmployeesAuthorities(dto));

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void updateAuthoritiesToRelatedPositionsTest() {
        User employee = createEmployee();
        Authority authority = getAuthority();
        List<Position> positions = getPositions();
        List<String> positionNames = List.of("Супер адмін");

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        when(positionRepo.findPositionsByNames(positionNames)).thenReturn(positions);
        when(authorityRepo.findAuthoritiesByPositions(positionNames)).thenReturn(List.of(getAuthority()));
        when(positionService.getEmployeeLoginPositionNames()).thenReturn(List.of("Супер адмін"));

        authorityService.updateAuthoritiesToRelatedPositions(EmployeePositionsDto.builder()
            .email(TEST_EMAIL)
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .name("Супер адмін")
                .build()))
            .build());
        authority.getEmployees().add(createAdmin());

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(positionRepo).findPositionsByNames(positionNames);
        verify(authorityRepo).findAuthoritiesByPositions(positionNames);
    }

    @Test
    void updateAuthoritiesToRelatedPositionsThrowsNotFoundExceptionTest() {
        var dto = new EmployeePositionsDto();
        assertThrows(UsernameNotFoundException.class, () -> authorityService.updateAuthoritiesToRelatedPositions(dto));
    }
}
