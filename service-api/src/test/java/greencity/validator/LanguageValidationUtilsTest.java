package greencity.validator;

import greencity.exception.exceptions.LanguageNotSupportedException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LanguageValidationUtilsTest {

    @Test
    void testIsValidWithSupportedLanguage() {
        assertTrue(LanguageValidationUtils.isValid("en"));
        assertTrue(LanguageValidationUtils.isValid("ua"));
    }

    @Test
    void testIsValidWithUnsupportedLanguage() {
        assertFalse(LanguageValidationUtils.isValid("es"));
        assertFalse(LanguageValidationUtils.isValid("de"));
        LanguageValidationUtils.isValid("   ");
        LanguageValidationUtils.isValid("");
    }

    @Test
    void testIsValidWithNullLanguage() {
        assertThrows(LanguageNotSupportedException.class, () -> {
            LanguageValidationUtils.isValid(null);
        });
    }
}