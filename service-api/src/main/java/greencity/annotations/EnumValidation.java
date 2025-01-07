package greencity.annotations;

import greencity.enums.ProfilePrivacyPolicy;
import greencity.validator.EnumValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface EnumValidation {
    Class<ProfilePrivacyPolicy> enumClass();

    String message() default "Invalid enum value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
