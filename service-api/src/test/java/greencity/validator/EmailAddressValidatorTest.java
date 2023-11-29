package greencity.validator;

import greencity.exception.exceptions.WrongEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailAddressValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "user123@mail.co", "john.doe123@company.org"})
    void testValidEmail(String email) {
        assertDoesNotThrow(() -> EmailAddressValidator.validate(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@company", "test@com", "missing.at.sign.example.com"})
    void testInvalidEmail(String invalidEmail) {
        assertThrows(WrongEmailException.class, () -> {
            EmailAddressValidator.validate(invalidEmail);
        });
    }

    @Test
    void testvalidateWithNullLanguage() {
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