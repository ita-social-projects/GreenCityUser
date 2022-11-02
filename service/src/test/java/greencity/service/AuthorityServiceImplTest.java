package greencity.service;

import greencity.ModelUtils;
import greencity.entity.*;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import greencity.security.service.AuthorityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorityServiceImplTest {
    @Mock
    UserRepo userRepo;
    @Mock
    AuthorityRepo authorityRepo;
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
    }

    @Test
    void updateEmployeesAuthoritiesTest() {
        User user = createUbsAdmin();
        User employee = createEmployee();
        Authority authority = getAuthority();

        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.of(user));
        when(userRepo.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(authorityRepo.findByName("test")).thenReturn(Optional.of(authority));

        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto(), "taras@gmail.com");
        authority.getEmployees().add(createAdmin());
        verify(authorityRepo).save(authority);
    }

}
