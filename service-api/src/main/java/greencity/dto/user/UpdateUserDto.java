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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
@SuperBuilder
public class UpdateUserDto {
    private Long id;
    private String name;
    private UserUpdateType userUpdateType;
}
