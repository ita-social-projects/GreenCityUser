package greencity.exception.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoogleApiExceptionTest {
    @Test
    void testGoogleApiExceptionMessage() {
        String message = "Test exception message";
        GoogleApiException exception = new GoogleApiException(message);
        assertEquals(message, exception.getMessage());
    }
}
