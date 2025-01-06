package greencity.validator;

import greencity.annotations.EnumValidation;
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

    private enum TestEnum {
        FIRST_VALUE,
        SECOND_VALUE
    }

    @BeforeEach
    void setUp() {
        enumValidator = new EnumValidatorImpl();
        mockContext = mock(ConstraintValidatorContext.class);

        EnumValidation mockAnnotation = mock(EnumValidation.class);

        when(mockAnnotation.enumClass()).thenAnswer(invocation -> TestEnum.class);

        enumValidator.initialize(mockAnnotation);
    }

    @Test
    void IsValidWithValidValueTest() {
        assertTrue(enumValidator.isValid("FIRST_VALUE", mockContext));
        assertTrue(enumValidator.isValid("SECOND_VALUE", mockContext));
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
        assertFalse(enumValidator.isValid("first_value", mockContext));
    }
}
