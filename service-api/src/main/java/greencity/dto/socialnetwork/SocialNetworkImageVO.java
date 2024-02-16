package greencity.dto.socialnetwork;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialNetworkImageVO {
    private Long id;
    private String imagePath;
    private String hostPath;
}
