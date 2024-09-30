package greencity.annotations;

import greencity.validator.PlaceStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PlaceStatusValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlaceStatus {
    String message() default "Place status must be 'approved' or 'declined'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}