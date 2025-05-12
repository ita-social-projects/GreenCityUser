package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
public class UserVOReducedDto {
    private Long id;

    private String name;

    private String email;

    private Role role;

    private UserStatus userStatus;

    private VerifyEmailVO verifyEmail;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;

    private ProfilePrivacyPolicy showLocation;

    private ProfilePrivacyPolicy showEcoPlace;

    private ProfilePrivacyPolicy showToDoList;

    private LocalDateTime lastActivityTime;

    private LanguageVO languageVO;

    private String firstName;

    @JsonManagedReference
    private UserNotificationPreferenceDto userNotificationPreferenceDto;
}
