package greencity.validator;

import greencity.exception.exceptions.validation.BadSocialNetworkLinksException;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialNetworkLinksValidatorTest {
    @InjectMocks
    SocialNetworkLinksValidator socialNetworkLinksValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    void isValid() {
        List<String> links = List.of("1", "2", "3", "4", "5", "6");
        assertThrows(BadSocialNetworkLinksException.class,
            () -> socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }

    @Test
    void isValidWithLinksNull() {
        List<String> links = null;
        assertTrue(socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }

    @Test
    void isValidWithTwoSameSocialNetworkLinks() {
        List<String> links = List.of("1", "1");
        assertThrows(BadSocialNetworkLinksException.class,
            () -> socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }

    @Test
    void isValidAllValidUrlsCorrect() {
        List<String> links = List.of("https://example1.com", "https://example2.com", "https://example3.com");
        assertTrue(socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }
}
