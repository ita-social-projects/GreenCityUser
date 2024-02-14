package greencity.validator;

import static greencity.ModelUtils.getUrl;
import greencity.exception.exceptions.InvalidURLException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlValidatorTest {

    @Test
    void UrlValidatorTrueTest() throws MalformedURLException {
        String url = getUrl().toString();
        assertTrue(UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorMalformedURLExceptionTest() {
        String url = "ttt://";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorURISyntaxExceptionTest() {
        String url = "http:// .";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url));
    }
}
