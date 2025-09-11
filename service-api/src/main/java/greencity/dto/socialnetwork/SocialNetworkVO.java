package greencity.dto.socialnetwork;

import greencity.dto.user.UserVO;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class SocialNetworkVO {
    private Long id;

    @Size(min = 1, max = 500)
    String url;

    SocialNetworkImageVO socialNetworkImage;

    UserVO user;
}
