package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.enums.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserUpdateDto {
    @Pattern(
        regexp = ValidationConstants.USERNAME_REGEXP,
        message = ValidationConstants.USERNAME_MESSAGE)
    private String name;

    @NotNull
    private EmailNotification emailNotification;
}
