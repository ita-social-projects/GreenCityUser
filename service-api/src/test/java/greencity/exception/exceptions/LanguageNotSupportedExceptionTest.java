package greencity.exception.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LanguageNotSupportedExceptionTest {
    @Test
    void testDefaultConstructor() {
        LanguageNotSupportedException exception = new LanguageNotSupportedException();
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        LanguageNotSupportedException exception = new LanguageNotSupportedException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMessageConstructorWithMockito() {
        String message = "Custom exception message";
        LanguageNotSupportedException exception = Mockito.spy(new LanguageNotSupportedException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage(); // Verify that getMessage() was called
    }
}