package greencity.security.service;

import greencity.dto.UpdateEmployeeAuthoritiesDto;
import greencity.dto.position.PositionDto;
import greencity.entity.Authority;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorityServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthorityRepo authorityRepo;
    @InjectMocks
    private AuthorityServiceImpl authorityService;

    @Test
    void getAllEmployeesAuthoritiesTest() {
        Set<String> expected = new HashSet<>();
        expected.add("test");
        expected.add("test1");
        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.ofNullable(getUser()));
        when(authorityRepo.getAuthoritiesByEmployeeId(getUser().getId())).thenReturn(expected);
        assertEquals(expected, authorityService.getAllEmployeesAuthorities("taras@gmail.com"));

        verify(userRepo).findByEmail("taras@gmail.com");
        verify(authorityRepo).getAuthoritiesByEmployeeId(getUser().getId());
    }

    @Test
    void updateEmployeesAuthoritiesTest() {
        User employee = createEmployee();
        List<Authority> authority = List.of(getAuthority());
        List<String> authoritiesName = authority.stream().map(Authority::getName)
            .collect(Collectors.toList());

        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.of(employee));
        when(authorityRepo.findAuthoritiesByNames(authoritiesName)).thenReturn(authority);
        employee.setAuthorities(authority);
        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto());

        verify(userRepo).save(employee);
    }

    @Test
    void updateEmployeesWithEmptyAuthoritiesListTest() {
        User employee = createEmployee();
        List<Authority> authority = List.of(getAuthority());
        List<String> authoritiesName = authority.stream().map(Authority::getName)
            .collect(Collectors.toList());

        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.of(employee));
        when(authorityRepo.findAuthoritiesByNames(authoritiesName)).thenReturn(authority);
        employee.setAuthorities(authority);
        authorityService.updateEmployeesAuthorities(getUserEmployeeWithNoAuthorityDto());

        verify(userRepo).save(employee);
    }

    @Test
    void updateEmployeesAuthoritiesTestBadRequestException() {
        User employee = createEmployee();
        employee.setRole(Role.ROLE_USER);

        var dto = getUserEmployeeAuthorityDto();

        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.of(employee));

        assertEquals(Role.ROLE_USER, employee.getRole());
        assertThrows(BadRequestException.class,
            () -> authorityService.updateEmployeesAuthorities(dto));
    }

    @Test
    void updateAuthoritiesTest() {
        User employee = createEmployee();
        Authority authority = getAuthority();
        when(userRepo.findByEmail("taras@mail.com")).thenReturn(Optional.of(employee));
        authorityService.updateAuthorities(UpdateEmployeeAuthoritiesDto.builder()
            .email("taras@mail.com")
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .name("test")
                .build()))
            .build());
        authority.getEmployees().add(createAdmin());
        verify(userRepo).findByEmail("taras@mail.com");
    }

    @Test
    void updateAuthoritiesUsernameNotFoundExceptionTest() {
        var dto = new UpdateEmployeeAuthoritiesDto();
        assertThrows(UsernameNotFoundException.class, () -> authorityService.updateAuthorities(dto));
    }
}
