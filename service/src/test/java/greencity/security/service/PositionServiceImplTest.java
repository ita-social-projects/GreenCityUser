package greencity.security.service;

import static greencity.ModelUtils.TEST_EMAIL;
import static greencity.ModelUtils.getEmployeeWithPositionsAndRelatedAuthorities;
import static greencity.ModelUtils.getEmployeeWithPositionsAndRelatedAuthorities_Empty;
import static greencity.ModelUtils.getPositionAuthoritiesDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import java.util.Collections;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PositionServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthorityRepo authorityRepo;
    @Mock
    private Authentication auth;
    @InjectMocks
    private PositionServiceImpl positionService;

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
    void getPositionsAndRelatedAuthoritiesTest() {
        User employee = getEmployeeWithPositionsAndRelatedAuthorities();
        var expected = getPositionAuthoritiesDto();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));

        assertEquals(expected, positionService.getPositionsAndRelatedAuthorities(TEST_EMAIL));

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void getPositionsAndRelatedAuthoritiesTest_AuthorotiesListEmpty() {
        User employee = getEmployeeWithPositionsAndRelatedAuthorities_Empty();
        employee.setAuthorities(Collections.emptyList());
        var expected = getPositionAuthoritiesDto();
        expected.setAuthorities(Collections.emptyList());
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        when(authorityRepo.findAuthoritiesByPositions(Collections.emptyList()))
            .thenReturn(Collections.emptyList());

        assertEquals(expected, positionService.getPositionsAndRelatedAuthorities(TEST_EMAIL));

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void getPositionsAndRelatedAuthoritiesThrowsNotFoundExceptionTest() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> positionService.getPositionsAndRelatedAuthorities(TEST_EMAIL));
        verify(userRepo).findByEmail(TEST_EMAIL);
    }
}
