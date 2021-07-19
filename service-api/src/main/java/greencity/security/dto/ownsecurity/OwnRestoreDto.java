package greencity.security.dto.ownsecurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static greencity.constant.ValidationConstants.INVALID_PASSWORD;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnRestoreDto {
    @NotBlank
    @Pattern(
        regexp = "^(?=.{0,19}[a-z]+)(?=.{0,19}[A-Z]+)(?=.{0,19}\\d+)"
            + "(?=.{0,19}[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,20}$",
        message = INVALID_PASSWORD)
    private String password;

    @NotBlank
    @Pattern(
        regexp = "^(?=.{0,19}[a-z]+)(?=.{0,19}[A-Z]+)(?=.{0,19}\\d+)"
            + "(?=.{0,19}[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,20}$",
        message = INVALID_PASSWORD)
    private String confirmPassword;

    @NotBlank
    private String token;
}
