package greencity.exception.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LanguageNotFoundExceptionTest {
    @Test
    void testDefaultConstructor() {
        LanguageNotFoundException exception = new LanguageNotFoundException();
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testMessageConstructor() {
        String message = "Custom exception message";
        LanguageNotFoundException exception = new LanguageNotFoundException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMessageConstructorWithMockito() {
        String message = "Custom exception message";
        LanguageNotFoundException exception = Mockito.spy(new LanguageNotFoundException(message));
        String actualMessage = exception.getMessage();
        assertEquals(message, actualMessage);
        verify(exception).getMessage(); // Verify that getMessage() was called
    }
}