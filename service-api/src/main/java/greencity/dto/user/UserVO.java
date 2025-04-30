package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserVO {
    private Long id;

    private String name;

    private String email;

    private Role role;

    private String userCredo;

    private UserStatus userStatus;

    private VerifyEmailVO verifyEmail;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;

    private String profilePicturePath;

    private ProfilePrivacyPolicy showLocation;

    private ProfilePrivacyPolicy showEcoPlace;

    private ProfilePrivacyPolicy showToDoList;

    private LocalDateTime lastActivityTime;

    private Long languageId;

    private String firstName;

    @JsonManagedReference
    private UserNotificationPreferenceDto userNotificationPreferenceDto;
}
