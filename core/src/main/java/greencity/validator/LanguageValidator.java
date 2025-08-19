package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private final LanguageService languageService;
    private List<String> codes;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        try {
            codes = languageService.findAllLanguageCodes();
        } catch (Exception e) {
            log.warn("Occurred error during processing request: {}", e.getMessage());
            codes = List.of("en", "uk");
        }
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
