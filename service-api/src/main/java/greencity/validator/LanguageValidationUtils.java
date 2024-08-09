package greencity.validator;

import greencity.exception.exceptions.LanguageNotSupportedException;
import java.util.List;
import java.util.Locale;

/**
 * This class should validate language.
 *
 * @author Volodymyr Mladonov
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
        if (language == null) {
            throw new LanguageNotSupportedException("Language couldn't be blank");
        }
        return SUPPORTED_LANGUAGES.contains(language.toLowerCase(Locale.ROOT));
    }
}
