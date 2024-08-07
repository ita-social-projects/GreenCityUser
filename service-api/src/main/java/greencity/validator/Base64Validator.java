package greencity.validator;

import greencity.annotations.ValidBase64;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;

public class Base64Validator implements ConstraintValidator<ValidBase64, String> {
    @Override
    public void initialize(ValidBase64 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String base64, ConstraintValidatorContext constraintValidatorContext) {
        if (base64 == null || base64.isEmpty()) {
            return true;
        }
        try {
            Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
