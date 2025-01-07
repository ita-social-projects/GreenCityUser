package greencity.validator;

import greencity.annotations.EnumValidation;
import greencity.enums.ProfilePrivacyPolicy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidation, String> {
    private Class<ProfilePrivacyPolicy> enumClass;

    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object enumValue : enumConstants) {
            if (enumValue.toString().equals(s)) {
                return true;
            }
        }
        return false;
    }
}
