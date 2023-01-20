package greencity.security.dto.ownsecurity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.constant.ValidationConstants;
import greencity.dto.position.PositionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSignUpDto {
    @NotBlank
    @Length(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = ValidationConstants.INVALID_EMAIL)
    private String email;

    private String uuid;

    private List<PositionDto> positions;

    @JsonIgnore
    private String password;
    @JsonProperty("isUbs")
    private boolean isUbs;
}
