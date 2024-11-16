package greencity.security.dto.ownsecurity;

import greencity.constant.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TestersSignInRequest(
    @NotBlank @Email(regexp = ValidationConstants.EMAIL_REGEXP,
        message = ValidationConstants.INVALID_EMAIL) String email,
    @NotBlank String password,
    @NotBlank String secretKey) {
}
