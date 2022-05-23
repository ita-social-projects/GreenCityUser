package greencity.validator;

import java.util.List;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import greencity.annotations.ValidLanguage;
import greencity.client.RestClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;
    private final RestClient restClient;

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
