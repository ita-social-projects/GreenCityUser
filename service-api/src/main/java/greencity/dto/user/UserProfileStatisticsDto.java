package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserProfileStatisticsDto {
    private Long amountHabitsInProgress;
    private Long amountHabitsAcquired;
    private Long amountPublishedNews;
    private Long amountOrganizedAndAttendedEvents;
}
