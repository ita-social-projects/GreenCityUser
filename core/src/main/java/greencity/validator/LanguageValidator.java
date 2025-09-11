package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.client.RestClient;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private final RestClient restClient;
    private List<String> codes;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        try {
            codes = restClient.getAllLanguageCodes();
        } catch (Exception e) {
            log.warn("Occurred error during processing request: {}", e.getMessage());
            codes = List.of("en", "ua");
        }
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
