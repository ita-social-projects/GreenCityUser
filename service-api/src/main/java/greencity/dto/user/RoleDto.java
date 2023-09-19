package greencity.dto.user;

import greencity.enums.Role;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RoleDto {
    private Role[] roles;
}
