package greencity.service;

import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;

import java.util.List;

public interface UserNotificationPreferenceService {

    List<UserNotificationPreferenceVO> findAllByUserId(Long id);

    boolean existsByUserIdAndEmailPreferenceAndPeriodicity(Long id, EmailPreference emailPreference,
                                                           EmailPreferencePeriodicity periodicity);
}
