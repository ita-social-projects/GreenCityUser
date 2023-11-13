package greencity.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageValidationUtilsTest {

    @Test
    void testIsValidWithSupportedLanguage() {
        assertTrue(LanguageValidationUtils.isValid("en"));
        assertTrue(LanguageValidationUtils.isValid("ua"));
    }

    @Test
    void testIsValidWithUnsupportedLanguage() {
        assertFalse(LanguageValidationUtils.isValid("es"));
        assertFalse(LanguageValidationUtils.isValid("de"));
    }

    @Test
    void testIsValidWithNullLanguage() {
        assertThrows(NullPointerException.class, () -> {
            LanguageValidationUtils.isValid(null);
        });
    }

    @Test
    void testIsValidWithEmptyLanguage() {
        assertThrows(IllegalArgumentException.class, () -> {
            LanguageValidationUtils.isValid("");
        });
    }

    @Test
    void testIsValidWithBlankLanguage() {
        assertThrows(IllegalArgumentException.class, () -> {
            LanguageValidationUtils.isValid("   ");
        });
    }
}