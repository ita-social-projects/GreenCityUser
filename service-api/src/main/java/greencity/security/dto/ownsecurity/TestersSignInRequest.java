package greencity.security.dto.ownsecurity;

import greencity.constant.ValidationConstants;
import greencity.enums.ProjectName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TestersSignInRequest(
    @NotBlank @Email(regexp = ValidationConstants.EMAIL_REGEXP,
        message = ValidationConstants.INVALID_EMAIL) String email,
    @NotBlank String password,
    @NotNull ProjectName projectName,
    @NotBlank String secretKey) {
}
