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
        regexp = "^(?=.{0,23}[a-z]+)(?=.{0,23}[A-Z]+)(?=.{0,23}\\d+)(?=.{0,23}[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,24}$",
        message = INVALID_PASSWORD)
    private String password;

    @NotBlank
    @Pattern(
        regexp = "^(?=.{0,23}[a-z]+)(?=.{0,23}[A-Z]+)(?=.{0,23}\\d+)(?=.{0,23}[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,24}$",
        message = INVALID_PASSWORD)
    private String confirmPassword;

    @NotBlank
    private String token;
}
