package greencity.security.dto.ownsecurity;

import greencity.annotations.PasswordValidation;
import greencity.constant.ValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordDto {
    @NotBlank
    @PasswordValidation
    private String currentPassword;
    @NotBlank
    @PasswordValidation
    private String password;

    @NotBlank
    @PasswordValidation
    private String confirmPassword;
}
