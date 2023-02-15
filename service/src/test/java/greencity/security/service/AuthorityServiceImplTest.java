package greencity.security.service;

import greencity.dto.UpdateEmployeeAuthoritiesDto;
import greencity.dto.position.PositionDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.Authority;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

//    @Test
//    void updateEmployeesAuthoritiesTest() {
//        User user = createUbsAdmin();
//        User employee = createEmployee();
//        Authority authority = getAuthority();
//
//        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.of(employee));
//        when(userRepo.findByEmail("email@mail.com")).thenReturn(Optional.of(user));
//        when(authorityRepo.findByName("test")).thenReturn(Optional.of(authority));
//
//        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto());
//        authority.getEmployees().add(createAdmin());
//        verify(authorityRepo).save(authority);
//    }

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

//    @Test
//    void updateEmployeesAuthoritiesNotFoundExceptionTest() {
//        var dto = UserEmployeeAuthorityDto.builder()
//            .employeeEmail("email@mail.com")
//            .authorities(List.of("test"))
//            .build();
//        User employee = createEmployee();
//        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(employee));
//        when(userRepo.findByEmail(dto.getEmployeeEmail())).thenReturn(Optional.empty());
//        assertThrows(NotFoundException.class,
//            () -> authorityService.updateEmployeesAuthorities(dto));
//        verify(userRepo).findByEmail("test@mail.com");
//    }
//
//    @Test
//    void updateEmployeesAuthoritiesBadRequestExceptionTest() {
//        User user = createUbsAdmin();
//        User employee = createEmployee();
//        when(userRepo.findByEmail("email@mail.com")).thenReturn(Optional.of(user));
//        when(userRepo.findByEmail(employee.getEmail())).thenReturn(Optional.empty());
//        var dto = UserEmployeeAuthorityDto.builder()
//            .employeeEmail("email@mail.com")
//            .authorities(List.of("test"))
//            .build();
//        assertThrows(BadRequestException.class,
//            () -> authorityService.updateEmployeesAuthorities(dto));
//        verify(userRepo, times(2)).findByEmail("email@mail.com");
//    }
}
