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
        // Arrange
        LanguageNotFoundException exception = new LanguageNotFoundException();

        // Act & Assert
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testMessageConstructor() {
        // Arrange
        String message = "Custom exception message";
        LanguageNotFoundException exception = new LanguageNotFoundException(message);

        // Act & Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMessageConstructorWithMockito() {
        // Arrange
        String message = "Custom exception message";
        LanguageNotFoundException exception = Mockito.spy(new LanguageNotFoundException(message));

        // Act
        String actualMessage = exception.getMessage();

        // Assert
        assertEquals(message, actualMessage);
        verify(exception).getMessage(); // Verify that getMessage() was called
    }
}