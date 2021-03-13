package greencity.dto.user;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserDeactivationReasonDto {
    private String email;
    private String name;
    private List<String> deactivationReasons;
    private String lang;
}
