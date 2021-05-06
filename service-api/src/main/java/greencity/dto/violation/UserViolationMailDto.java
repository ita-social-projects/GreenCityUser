package greencity.dto.violation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserViolationMailDto {
    private String name;
    private String email;
    private String violationDescription;
}
