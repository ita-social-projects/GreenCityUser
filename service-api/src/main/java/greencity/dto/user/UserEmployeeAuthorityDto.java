package greencity.dto.user;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmployeeAuthorityDto {
    private String employeeEmail;
    private List<String> authorities;
}
