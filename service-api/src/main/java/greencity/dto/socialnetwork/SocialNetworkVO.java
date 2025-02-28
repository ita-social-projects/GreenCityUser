package greencity.dto.socialnetwork;

import greencity.dto.user.UserVO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SocialNetworkVO {
    private Long id;

    @Size(min = 1, max = 500)
    String url;

    SocialNetworkImageVO socialNetworkImage;

    UserVO user;
}
