package greencity.service;

import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementUserStatisticsServiceImpl implements ManagementUserStatisticsService {

    private final UserRepo userRepo;

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
    public List<UserLocationStatisticDto> getUserLocationsDistribution(String groupBy) {
        return switch (groupBy) {
            case "city" -> userRepo.getUserLocationsDistributionByCity();
            case "region" -> userRepo.getUserLocationsDistributionByRegion();
            case "country" -> userRepo.getUserLocationsDistributionByCountry();
            default -> userRepo.getUserLocationsDistributionByCity();
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        return userRepo.getUserEmailPreferencesDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countActiveUsers() {
        return userRepo.countActiveUsers();
    }
}
