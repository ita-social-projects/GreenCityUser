package greencity.repository;

import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationPreferenceRepo extends JpaRepository<UserNotificationPreference, Long> {
    boolean existsByUserEmailAndEmailPreferenceAndPeriodicity(String userEmail, EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity);
}
