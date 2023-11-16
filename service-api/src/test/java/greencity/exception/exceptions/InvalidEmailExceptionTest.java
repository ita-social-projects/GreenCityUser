package greencity.exception.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvalidEmailExceptionTest {

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        InvalidEmailException exception = Mockito.spy(new InvalidEmailException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage();
    }
}