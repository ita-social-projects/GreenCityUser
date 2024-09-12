package greencity.repository;

import greencity.entity.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface UserNotificationPreferenceRepo extends JpaRepository<UserNotificationPreference, Long> {
    void deleteAllByUserId(Long id);

    Set<UserNotificationPreference> findAllByUserId(Long id);
}
