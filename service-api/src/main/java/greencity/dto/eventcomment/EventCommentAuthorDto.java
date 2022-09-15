package greencity.dto.eventcomment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCommentAuthorDto {
    private Long id;

    private String name;

    private String userProfilePicturePath;
}
