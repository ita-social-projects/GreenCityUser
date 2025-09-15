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
        String email = "test@gmailcom";
        EmailPreference emailPreference = EmailPreference.LIKES;
        EmailPreferencePeriodicity periodicity = EmailPreferencePeriodicity.WEEKLY;
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            email, emailPreference, periodicity);
        boolean expectedResult = true;

        when(userNotificationPreferenceRepo.existsByUserEmailAndEmailPreferenceAndPeriodicity(email, emailPreference,
            periodicity))
                .thenReturn(expectedResult);

        boolean actualResult =
            userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(emailPreferenceDto);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void existsByUserIdAndEmailPreferenceAndPeriodicityWhenNotExistsTest() {
        String email = "test@gmailcom";
        EmailPreference emailPreference = EmailPreference.LIKES;
        EmailPreferencePeriodicity periodicity = EmailPreferencePeriodicity.WEEKLY;
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            email, emailPreference, periodicity);
        boolean expectedResult = false;

        when(userNotificationPreferenceRepo.existsByUserEmailAndEmailPreferenceAndPeriodicity(email, emailPreference,
            periodicity))
                .thenReturn(expectedResult);

        boolean actualResult =
            userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(emailPreferenceDto);

        assertEquals(expectedResult, actualResult);
    }
}
