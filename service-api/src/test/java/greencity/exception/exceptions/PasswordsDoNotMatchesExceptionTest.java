package greencity.exception.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordsDoNotMatchesExceptionTest {

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        PasswordsDoNotMatchesException exception = Mockito.spy(new PasswordsDoNotMatchesException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage();
    }
}