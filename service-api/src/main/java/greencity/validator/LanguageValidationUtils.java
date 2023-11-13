package greencity.validator;

import greencity.exception.exceptions.LanguageNotSupportedException;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Locale;

/**
 * This class should validate language.
 */
public class LanguageValidationUtils {
    private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "ua");

    /**
     * This method validate language.
     *
     * @param language language to be validated
     * @return true if everything is ok
     */
    public static boolean isValid(String language) {
        Validate.notBlank(language, "Language couldn't be blank");
        return SUPPORTED_LANGUAGES.contains(language.toLowerCase(Locale.ROOT));
    }
}
