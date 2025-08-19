package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoResponse {
    private String profilePicturePath;
    private String name;
    private String userCredo;
    private List<SocialNetworkResponseDTO> socialNetworks;
    private ProfilePrivacyPolicy showLocation;
    private ProfilePrivacyPolicy showEcoPlace;
    private ProfilePrivacyPolicy showToDoList;
    private Double rating;
    private Role role;
    private UserLocationDto userLocationDto;
    private Set<UserNotificationPreferenceDto> notificationPreferences;
}
