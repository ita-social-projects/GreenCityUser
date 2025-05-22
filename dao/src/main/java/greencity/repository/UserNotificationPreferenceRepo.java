package greencity.repository;

import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationPreferenceRepo extends JpaRepository<UserNotificationPreference, Long> {
    boolean existsByUserIdAndEmailPreferenceAndPeriodicity(Long id, EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity);
}
