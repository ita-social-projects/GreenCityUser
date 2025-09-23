package greencity.security.dto.ownsecurity;

import greencity.constant.ValidationConstants;
import greencity.enums.ProjectName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignInDto {
    @NotBlank
    @Email(regexp = ValidationConstants.EMAIL_REGEXP, message = ValidationConstants.INVALID_EMAIL)
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private ProjectName projectName;
}
