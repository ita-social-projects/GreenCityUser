package greencity.validator;

import greencity.exception.exceptions.LanguageNotSupportedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageValidationUtilsTest {

    @Test
    public void testIsValidWithSupportedLanguage() {
        assertTrue(LanguageValidationUtils.isValid("en"));
        assertTrue(LanguageValidationUtils.isValid("ua"));
    }

    @Test
    public void testIsValidWithUnsupportedLanguage() {
        assertFalse(LanguageValidationUtils.isValid("es"));
        assertFalse(LanguageValidationUtils.isValid("de"));
    }

    @Test
    public void testIsValidWithNullLanguage() {
        assertThrows(NullPointerException.class, () -> {
            LanguageValidationUtils.isValid(null);
        });
    }

    @Test
    public void testIsValidWithEmptyLanguage() {
        assertThrows(IllegalArgumentException.class, () -> {
            LanguageValidationUtils.isValid("");
        });
    }

    @Test
    public void testIsValidWithBlankLanguage() {
        assertThrows(IllegalArgumentException.class, () -> {
            LanguageValidationUtils.isValid("   ");
        });
    }
}