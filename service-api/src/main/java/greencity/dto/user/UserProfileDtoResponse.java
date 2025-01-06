package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import java.util.List;

import greencity.enums.EcoPlacePrivacyPolicy;
import greencity.enums.LocationPrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.ToDoListPrivacyPolicy;
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
    private LocationPrivacyPolicy showLocation;
    private EcoPlacePrivacyPolicy showEcoPlace;
    private ToDoListPrivacyPolicy showShoppingList;
    private Float rating;
    private Role role;
    private UserLocationDto userLocationDto;
}
