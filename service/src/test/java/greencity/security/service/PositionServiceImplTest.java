package greencity.security.service;

import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void getEmployeeLoginPositionNamesTest() {
        User employee = createEmployee();
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        assertEquals(List.of("Супер адмін"), positionService.getEmployeeLoginPositionNames(TEST_EMAIL));
        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void getEmployeeLoginPositionNamesWithoutParametersTest() {
        User employee = createEmployee();
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        assertEquals(List.of("Супер адмін"), positionService.getEmployeeLoginPositionNames());
        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void getEmployeeLoginPositionNamesThrowsBadRequestExceptionTest() {
        assertThrows(BadRequestException.class,
            () -> positionService.getEmployeeLoginPositionNames("test@gmail.com"));
    }
}
