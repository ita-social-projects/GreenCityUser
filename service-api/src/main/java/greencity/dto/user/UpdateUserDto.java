package greencity.dto.user;

import greencity.enums.UserUpdateType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This class represents common fields for GreenCity and GreenCityUser user.
 * {@link UserVO} entity
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateUserDto extends CreateGreenCityUserDto{
    private UserUpdateType userUpdateType;
}
