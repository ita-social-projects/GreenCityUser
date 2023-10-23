package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import java.util.List;

import greencity.enums.Role;
import lombok.*;

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
    private String cityEn;
    private String cityUa;
    private String regionEn;
    private String regionUa;
    private String countryEn;
    private String countryUa;
    private Double latitude;
    private Double longitude;
}
