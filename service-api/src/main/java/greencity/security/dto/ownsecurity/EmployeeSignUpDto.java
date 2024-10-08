package greencity.security.dto.ownsecurity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.constant.ValidationConstants;
import greencity.dto.position.PositionDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSignUpDto {
    @Pattern(regexp = ValidationConstants.USERNAME_REGEXP, message = ValidationConstants.USERNAME_MESSAGE)
    private String name;
    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = ValidationConstants.INVALID_EMAIL)
    private String email;
    private String uuid;
    private List<PositionDto> positions;
    @JsonIgnore
    private String password;
    @JsonProperty("isUbs")
    private boolean isUbs;
}
