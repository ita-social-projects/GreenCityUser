package greencity.validator;

import greencity.exception.exceptions.WrongEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailAddressValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@company", "test@com", "missing.at.sign.example.com"})
    void testInvalidEmail(String invalidEmail) {
        assertThrows(WrongEmailException.class, () -> {
            EmailAddressValidator.validate(invalidEmail);
        });
    }

    @Test
    void testValidateWithNullLanguage() {
        assertThrows(NullPointerException.class, () -> {
            EmailAddressValidator.validate(null);
        });
    }

    @Test
    void testEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            EmailAddressValidator.validate("");
        });
    }
}