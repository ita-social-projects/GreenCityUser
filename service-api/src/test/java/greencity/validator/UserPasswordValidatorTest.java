package greencity.validator;

import greencity.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        String incorrect3 = "Qwerty-123";
        String incorrect4 = "Test-123456";

        when(context.buildConstraintViolationWithTemplate(any()))
            .thenReturn(ModelUtils.getConstraintViolationBuilder());

        assertTrue(validator.isValid(correct1, context));
        assertTrue(validator.isValid(correct2, context));
        assertFalse(validator.isValid(incorrect1, context));
        assertFalse(validator.isValid(incorrect2, context));
        assertFalse(validator.isValid(incorrect3, context));
        assertFalse(validator.isValid(incorrect4, context));
    }
}