package greencity.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPasswordValidatorTest {
    @Mock
    private ConstraintValidatorContext context;
    @InjectMocks
    private UserPasswordValidator validator;

    @Test
    void isValid() {
        String correct1 = "String-123";
        String correct2 = "Sl12Ww14!";

        assertTrue(validator.isValid(correct1, context));
        assertTrue(validator.isValid(correct2, context));
    }
}