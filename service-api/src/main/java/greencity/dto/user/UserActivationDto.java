package greencity.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserActivationDto {
    private String email;
    private String name;
    private String lang;
}
