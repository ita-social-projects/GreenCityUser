package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import greencity.enums.EmailPreference;
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
public class UserNotificationPreferenceDto {
    private Long id;

    @JsonBackReference
    private UserVO userVO;

    private EmailPreference emailPreference;
}
