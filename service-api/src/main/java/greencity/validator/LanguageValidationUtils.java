package greencity.validator;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.LanguageNotSupportedException;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class should validate language.
 * 
 * @author Volodymyr Mladonov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
            throw new LanguageNotSupportedException(ErrorMessage.LANGUAGE_IS_EMPTY);
        }
        return SUPPORTED_LANGUAGES.contains(language.toLowerCase(Locale.ROOT));
    }
}
