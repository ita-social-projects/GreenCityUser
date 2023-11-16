package greencity.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailAddressValidatorTest {
    @Test
    void testValidEmail() {
        assertTrue(EmailAddressValidator.isValid("test@example.com"));
        assertTrue(EmailAddressValidator.isValid("user123@mail.co"));
        assertTrue(EmailAddressValidator.isValid("john.doe123@company.org"));
    }

    @Test
    void testInvalidEmail() {
        assertFalse(EmailAddressValidator.isValid("invalid-email"));
        assertFalse(EmailAddressValidator.isValid("user@company"));
        assertFalse(EmailAddressValidator.isValid("test@com"));
        assertFalse(EmailAddressValidator.isValid("missing.at.sign.example.com"));
    }

    @Test
    void testIsValidWithNullLanguage() {
        assertThrows(NullPointerException.class, () -> {
            EmailAddressValidator.isValid(null);
        });
    }

    @Test
    void testEmptyEmail() {
        assertFalse(EmailAddressValidator.isValid(""));
    }
}