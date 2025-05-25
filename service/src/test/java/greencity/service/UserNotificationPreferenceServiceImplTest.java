package greencity.service;

import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.repository.UserNotificationPreferenceRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationPreferenceServiceImplTest {

    @Mock
    UserNotificationPreferenceRepo userNotificationPreferenceRepo;

    @InjectMocks
    UserNotificationPreferenceServiceImpl userNotificationPreferenceService;

    @Test
    void existsByUserIdAndEmailPreferenceAndPeriodicityTest() {
        Long userId = 1L;
        EmailPreference emailPreference = EmailPreference.LIKES;
        EmailPreferencePeriodicity periodicity = EmailPreferencePeriodicity.WEEKLY;
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            userId, emailPreference, periodicity);
        boolean expectedResult = true;

        when(userNotificationPreferenceRepo.existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
            periodicity))
                .thenReturn(expectedResult);

        boolean actualResult =
            userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(emailPreferenceDto);

        assertEquals(expectedResult, actualResult);
    }

}
