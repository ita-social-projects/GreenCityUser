package greencity.service;

import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.repository.UserNotificationPreferenceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationPreferenceServiceImpl implements UserNotificationPreferenceService {
    private final UserNotificationPreferenceRepo userNotificationPreferenceRepo;

    @Override
    public boolean existsByUserIdAndEmailPreferenceAndPeriodicity(EmailPreferenceDto emailPreferenceDto) {
        String userEmail = emailPreferenceDto.userEmail();
        EmailPreference emailPreference = emailPreferenceDto.emailPreference();
        EmailPreferencePeriodicity periodicity = emailPreferenceDto.emailPreferencePeriodicity();
        return userNotificationPreferenceRepo.existsByUserEmailAndEmailPreferenceAndPeriodicity(userEmail,
            emailPreference, periodicity);
    }
}
