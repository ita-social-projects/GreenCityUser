package greencity.service;

import greencity.entity.*;
import greencity.enums.Role;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import greencity.security.service.AuthorityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.util.*;

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
        User user = getUser();
        user.setRole(Role.ROLE_ADMIN);
        Authority authority = getAuthority();
        when(userRepo.findByEmail("taras@gmail.com")).thenReturn(Optional.ofNullable(user));
        when(userRepo.findById(createAdmin().getId())).thenReturn(Optional.of(createAdmin()));
        when(authorityRepo.findByName("test")).thenReturn(Optional.of(authority));
        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto(), "taras@gmail.com");
        authority.getEmployees().add(createAdmin());
        verify(authorityRepo).save(authority);
    }

}
