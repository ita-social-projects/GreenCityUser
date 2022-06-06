package greencity.dto.user;

import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Double rating;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    private List<UserVO> userFriends = new ArrayList<>();

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;

    private String profilePicturePath;

    private String city;

    private Boolean showLocation;

    private Boolean showEcoPlace;

    private Boolean showShoppingList;

    private LocalDateTime lastActivityTime;

    private LanguageVO languageVO;
}
