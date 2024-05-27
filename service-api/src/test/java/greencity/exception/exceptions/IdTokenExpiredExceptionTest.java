package greencity.exception.exceptions;

import greencity.constant.ErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdTokenExpiredExceptionTest {
    @Test
    void idTokenExpiredExceptionMessageTest() {
        String message = "Google id token is not valid or expired. ";
        var exception = new IdTokenExpiredException(message);
        Assertions.assertEquals(ErrorMessage.EXPIRED_GOOGLE_ID_TOKEN, exception.getMessage());
    }
}
