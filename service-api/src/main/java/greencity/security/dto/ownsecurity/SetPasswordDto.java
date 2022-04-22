package greencity.security.dto.ownsecurity;

import greencity.annotations.PasswordValidation;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class SetPasswordDto {
    @NotBlank
    @PasswordValidation
    private String password;

    @NotBlank
    @PasswordValidation
    private String confirmPassword;
}
