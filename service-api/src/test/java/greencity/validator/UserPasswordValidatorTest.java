package greencity.validator;

import greencity.ModelUtils;
import jakarta.validation.ConstraintValidatorContext;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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
        String incorrect1 = "short";
        String incorrect2 = "123456789";

        when(context.buildConstraintViolationWithTemplate(any()))
            .thenReturn(ModelUtils.getConstraintViolationBuilder());

        assertTrue(validator.isValid(correct1, context));
        assertTrue(validator.isValid(correct2, context));
        assertFalse(validator.isValid(incorrect1, context));
        assertFalse(validator.isValid(incorrect2, context));
    }
}