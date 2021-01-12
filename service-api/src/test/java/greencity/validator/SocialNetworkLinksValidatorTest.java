package greencity.validator;

import greencity.exception.exceptions.BadSocialNetworkLinksException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}
