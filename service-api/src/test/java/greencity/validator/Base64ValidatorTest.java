package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class Base64ValidatorTest {
    private Base64Validator base64Validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        base64Validator = new Base64Validator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidBase64() {
        String validBase64_1 = "SGVsbG8gd29ybGQ=";
        String validBase64_2 = "U29tZSB0ZXh0Lg==";
        String validBase64_3 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/rlGb8EAAAAASUVORK5CYII=";

        assertTrue(base64Validator.isValid(validBase64_1, context));
        assertTrue(base64Validator.isValid(validBase64_2, context));
        assertTrue(base64Validator.isValid(validBase64_3, context));
    }

    @Test
    void testInvalidBase64() {
        String invalidBase64_1 = "SGVsbG8gd29ybGQ@";
        String invalidBase64_2 = "U29tZSB0ZXh0.@#";
        String invalidBase64_3 = "====";

        assertFalse(base64Validator.isValid(invalidBase64_1, context));
        assertFalse(base64Validator.isValid(invalidBase64_2, context));
        assertFalse(base64Validator.isValid(invalidBase64_3, context));
    }

    @Test
    void testNullAndEmptyBase64() {
        assertTrue(base64Validator.isValid(null, context));
        assertTrue(base64Validator.isValid("", context));
    }
}
