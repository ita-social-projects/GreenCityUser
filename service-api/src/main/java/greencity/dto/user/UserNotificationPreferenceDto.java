package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserNotificationPreferenceDto {
    private Long id;

    @JsonBackReference
    private UserVO userVO;

    private EmailPreference emailPreference;

    private EmailPreferencePeriodicity periodicity;
}
