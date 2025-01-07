package greencity.validator;

import greencity.annotations.EnumValidation;
import greencity.enums.ProfilePrivacyPolicy;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
class EnumValidatorImplTest {
    private EnumValidatorImpl enumValidator;
    private ConstraintValidatorContext mockContext;

    @BeforeEach
    void setUp() {
        enumValidator = new EnumValidatorImpl();
        mockContext = mock(ConstraintValidatorContext.class);

        EnumValidation mockAnnotation = mock(EnumValidation.class);

        when(mockAnnotation.enumClass()).thenAnswer(invocation -> ProfilePrivacyPolicy.class);

        enumValidator.initialize(mockAnnotation);
    }

    @Test
    void IsValidWithValidValueTest() {
        assertTrue(enumValidator.isValid("PRIVATE", mockContext));
        assertTrue(enumValidator.isValid("FRIEND_ONLY", mockContext));
        assertTrue(enumValidator.isValid("PUBLIC", mockContext));
    }

    @Test
    void IsValidWithInvalidValueTest() {
        assertFalse(enumValidator.isValid("INVALID_VALUE", mockContext));
        assertFalse(enumValidator.isValid("", mockContext));
    }

    @Test
    void IsValidWithNullValueTest() {
        assertTrue(enumValidator.isValid(null, mockContext));
    }

    @Test
    void IsValidWithCaseSensitiveValueTest() {
        assertFalse(enumValidator.isValid("friends_only", mockContext));
    }
}
