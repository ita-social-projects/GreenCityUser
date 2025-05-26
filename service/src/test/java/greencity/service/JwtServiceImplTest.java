package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    JwtServiceImpl jwtService;

    @Test
    void findUserIdByEmailTest() {
        String email = "email";
        Long expectedResult = 4L;

        when(userRepo.findIdByEmail(email))
            .thenReturn(Optional.of(expectedResult));

        Long actualResult = jwtService.findUserIdByEmail(email);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findUserIdByEmailTestThrowsWrongEmailException() {
        String email = "email";
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findIdByEmail(email))
            .thenReturn(Optional.empty());

        WrongEmailException wrongEmailException = assertThrows(
            WrongEmailException.class,
            () -> jwtService.findUserIdByEmail(email));
        String actualExceptionMessage = wrongEmailException.getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }
}
