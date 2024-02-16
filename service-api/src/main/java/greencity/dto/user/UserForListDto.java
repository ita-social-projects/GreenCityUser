package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserForListDto {
    @NotNull
    private Long id;

    @Pattern(
        regexp = ValidationConstants.USERNAME_REGEXP,
        message = ValidationConstants.USERNAME_MESSAGE)
    private String name;

    private LocalDateTime dateOfRegistration;

    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;

    @NotNull
    private UserStatus userStatus;

    @NotNull
    private Role role;

    private String userCredo;
}
