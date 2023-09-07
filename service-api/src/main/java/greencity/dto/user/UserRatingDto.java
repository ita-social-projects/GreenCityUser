package greencity.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRatingDto {
    private Long id;

    private String email;

    private Double rating;
}
