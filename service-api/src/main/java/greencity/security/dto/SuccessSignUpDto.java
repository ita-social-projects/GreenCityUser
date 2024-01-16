package greencity.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuccessSignUpDto {
    private Long userId;
    private String username;
    private String email;
    private boolean ownRegistrations;
}
