package greencity.dto.user;

import greencity.enums.Role;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserAdminRegistrationDto {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime dateOfRegistration;
    private UserStatus userStatus;
    private Role role;
    private String languageCode;
}
