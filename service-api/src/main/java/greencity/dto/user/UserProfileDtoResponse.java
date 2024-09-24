package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
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
    private Boolean showLocation;
    private Boolean showEcoPlace;
    private Boolean showShoppingList;
    private Float rating;
    private Role role;
    private UserLocationDto userLocationDto;
    private Set<UserNotificationPreferenceDto> notificationPreferences;
}
