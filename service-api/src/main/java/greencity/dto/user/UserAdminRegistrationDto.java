package greencity.dto.user;

import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

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
