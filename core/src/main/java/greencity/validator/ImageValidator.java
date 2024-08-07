package greencity.validator;

import greencity.annotations.ImageValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ImageValidation, MultipartFile> {
    private final List<String> validType = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext constraintValidatorContext) {
        if (image == null) {
            return true;
        } else {
            return validType.contains(image.getContentType());
        }
    }
}
