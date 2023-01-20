package greencity.security.dto.ownsecurity;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.annotations.PasswordValidation;
import greencity.constant.ValidationConstants;
import greencity.dto.position.PositionDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @NotBlank
    @Length(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    @Pattern(
        regexp = "^[a-zA-Z0-9]+([._]?[a-zA-Z0-9]+)++$",
        message = ValidationConstants.INVALID_USERNAME)
    private String name;

    @NotBlank
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = ValidationConstants.INVALID_EMAIL)
    private String email;

    private String uuid;

    private List<PositionDto> positions;

    @NotBlank
    @PasswordValidation
    private String password;
    @JsonProperty("isUbs")
    private boolean isUbs;
}
