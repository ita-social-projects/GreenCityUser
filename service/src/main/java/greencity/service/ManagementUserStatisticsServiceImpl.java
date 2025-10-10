package greencity.service;

import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.enums.DateGranularity;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementUserStatisticsServiceImpl implements ManagementUserStatisticsService {
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(LocalDateTime startDate,
        LocalDateTime endDate, DateGranularity granularity) {
        String granularityStr = granularity.toString();
        return userRepo.countUsersByRegistrationDateBetween(startDate, endDate, granularityStr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserRoleStatisticDto> getUserRolesDistribution() {
        return userRepo.getUserRolesDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserStatusStatisticDto> getUserStatusesDistribution() {
        return userRepo.getUserStatusesDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        return userRepo.getUserEmailPreferencesDistribution();
    }
}
