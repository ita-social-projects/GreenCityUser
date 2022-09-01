package greencity.dto.user;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserEmployeeAuthorityDto {
    private Long employeeId;
    private List<String> authorities;
}
