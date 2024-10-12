package greencity.validator;

import greencity.annotations.ValidPlaceStatus;
import greencity.enums.PlaceStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlaceStatusValidator implements ConstraintValidator<ValidPlaceStatus, PlaceStatus> {
    @Override
    public boolean isValid(PlaceStatus placeStatus, ConstraintValidatorContext context) {
        return placeStatus == PlaceStatus.APPROVED || placeStatus == PlaceStatus.DECLINED;
    }
}