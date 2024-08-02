package greencity.exception.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.exception.exceptions.google.GoogleApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleApiExceptionTest {
    @Test
    void testGoogleApiExceptionMessage() {
        String message = "Test exception message";
        GoogleApiException exception = new GoogleApiException(message);
        assertEquals(message, exception.getMessage());
    }
}
