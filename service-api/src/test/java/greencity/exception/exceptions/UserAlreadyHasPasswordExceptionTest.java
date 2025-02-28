package greencity.exception.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAlreadyHasPasswordExceptionTest {

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        UserAlreadyHasPasswordException exception = Mockito.spy(new UserAlreadyHasPasswordException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage();
    }
}