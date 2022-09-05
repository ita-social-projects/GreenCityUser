package greencity.dto.user;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmployeeAuthorityDto {
    private Long employeeId;
    private List<String> authorities;
}
