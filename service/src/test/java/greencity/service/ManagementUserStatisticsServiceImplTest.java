package greencity.service;

import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.enums.DateGranularity;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagementUserStatisticsServiceImplTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    ManagementUserStatisticsServiceImpl managementUserStatisticsService;

    LocalDateTime startDate;
    LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
    }

    @Test
    void testGetUserRegistrationsByDateRange() {
        DateGranularity granularity = DateGranularity.MONTH;
        List<UserRegistrationStatisticDto> expectedStats = Arrays.asList(
            new UserRegistrationStatisticDto(LocalDateTime.now(), 10L),
            new UserRegistrationStatisticDto(LocalDateTime.now(), 15L));

        when(userRepo.countUsersByRegistrationDateBetween(startDate, endDate, granularity.toString()))
            .thenReturn(expectedStats);

        List<UserRegistrationStatisticDto> result = managementUserStatisticsService.getUserRegistrationsByDateRange(
            startDate, endDate, granularity);

        assertNotNull(result);
        assertEquals(expectedStats.size(), result.size());
        assertEquals(expectedStats, result);
        verify(userRepo).countUsersByRegistrationDateBetween(startDate, endDate, granularity.toString());
    }

    @Test
    void testGetUserRolesDistribution() {
        List<UserRoleStatisticDto> expectedStats = Arrays.asList(
            new UserRoleStatisticDto(Role.ROLE_ADMIN, 5L),
            new UserRoleStatisticDto(Role.ROLE_USER, 95L));

        when(userRepo.getUserRolesDistribution()).thenReturn(expectedStats);

        List<UserRoleStatisticDto> result = managementUserStatisticsService.getUserRolesDistribution();

        assertNotNull(result);
        assertEquals(expectedStats.size(), result.size());
        assertEquals(expectedStats, result);
        verify(userRepo).getUserRolesDistribution();
    }

    @Test
    void testGetUserStatusesDistribution() {
        List<UserStatusStatisticDto> expectedStats = Arrays.asList(
            new UserStatusStatisticDto(UserStatus.VERIFIED, 80L),
            new UserStatusStatisticDto(UserStatus.CREATED, 15L),
            new UserStatusStatisticDto(UserStatus.VERIFIED, 5L));

        when(userRepo.getUserStatusesDistribution()).thenReturn(expectedStats);

        List<UserStatusStatisticDto> result = managementUserStatisticsService.getUserStatusesDistribution();

        assertNotNull(result);
        assertEquals(expectedStats.size(), result.size());
        assertEquals(expectedStats, result);
        verify(userRepo).getUserStatusesDistribution();
    }

    @Test
    void testGetUserEmailPreferencesDistribution() {
        List<UserEmailPreferencesStatisticDto> expectedStats = Arrays.asList(
            new UserEmailPreferencesStatisticDto(EmailPreference.LIKES, EmailPreferencePeriodicity.DAILY, 5L),
            new UserEmailPreferencesStatisticDto(EmailPreference.COMMENTS, EmailPreferencePeriodicity.MONTHLY, 1L),
            new UserEmailPreferencesStatisticDto(EmailPreference.SYSTEM, EmailPreferencePeriodicity.DAILY, 7L));

        when(userRepo.getUserEmailPreferencesDistribution()).thenReturn(expectedStats);

        List<UserEmailPreferencesStatisticDto> result =
            managementUserStatisticsService.getUserEmailPreferencesDistribution();

        assertNotNull(result);
        assertEquals(expectedStats.size(), result.size());
        assertEquals(expectedStats, result);
        verify(userRepo).getUserEmailPreferencesDistribution();
    }
}
