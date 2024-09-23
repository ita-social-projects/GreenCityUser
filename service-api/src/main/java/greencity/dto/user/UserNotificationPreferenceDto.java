package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import greencity.entity.User;
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
    @JsonIgnore
    private User user;
    private EmailPreference emailPreference;
}
