package greencity.exception.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserAlreadyHasPasswordExceptionTest {

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        UserAlreadyHasPasswordException exception = new UserAlreadyHasPasswordException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMessageConstructorWithMockito() {
        String message = "Custom exception message";
        UserAlreadyHasPasswordException exception = Mockito.spy(new UserAlreadyHasPasswordException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage();
    }
}