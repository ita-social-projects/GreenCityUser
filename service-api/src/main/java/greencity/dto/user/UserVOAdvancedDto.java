package greencity.dto.user;

import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor()
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserVOAdvancedDto {
    private String firstName;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    private Long id;

    private String name;

    private String email;

    private Role role;

    private String userCredo;

    private UserStatus userStatus;

    private String profilePicturePath;

    private UserLocationDto userLocation;

    private Long languageId;
}
