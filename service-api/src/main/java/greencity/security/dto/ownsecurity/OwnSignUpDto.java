package greencity.security.dto.ownsecurity;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.annotations.PasswordValidation;
import greencity.constant.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @Pattern(
        regexp = ValidationConstants.USERNAME_REGEXP,
        message = ValidationConstants.USERNAME_MESSAGE)
    private String name;

    @NotBlank
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = ValidationConstants.INVALID_EMAIL)
    private String email;

    @NotBlank
    @PasswordValidation
    private String password;
    @JsonProperty("isUbs")
    private boolean isUbs;
}
