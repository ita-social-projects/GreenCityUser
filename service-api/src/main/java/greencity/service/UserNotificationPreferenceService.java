package greencity.service;

import greencity.dto.emailpreference.EmailPreferenceDto;

public interface UserNotificationPreferenceService {
    boolean existsByUserIdAndEmailPreferenceAndPeriodicity(EmailPreferenceDto emailPreferenceDto);
}
