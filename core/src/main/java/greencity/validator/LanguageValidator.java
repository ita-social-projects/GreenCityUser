package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.client.RestClient;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;
    private final RestClient restClient;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        codes = restClient.getAllLanguageCodes();
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
