package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetLocationForUserDto {
    Long id;
    UserProfileDtoRequest userProfileDtoRequest;
}
