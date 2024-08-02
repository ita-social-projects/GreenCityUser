package greencity.exception.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.exception.exceptions.resource.CheckRepeatingValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckRepeatingValueExceptionTest {

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        CheckRepeatingValueException exception = Mockito.spy(new CheckRepeatingValueException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage();
    }
}