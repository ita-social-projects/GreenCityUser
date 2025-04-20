package greencity.service;

import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import java.util.List;

public interface ManagementUserStatisticsService {
    /**
     * Method to get List of {@link UserRoleStatisticDto} to show distribution of
     * roles.
     *
     * @return {@link List} of {@link UserRoleStatisticDto}
     */
    List<UserRoleStatisticDto> getUserRolesDistribution();

    /**
     * Method to get List of {@link UserStatusStatisticDto} to show distribution of
     * statuses.
     *
     * @return {@link List} of {@link UserStatusStatisticDto}
     */
    List<UserStatusStatisticDto> getUserStatusesDistribution();

    /**
     * Method to get List of {@link UserLocationStatisticDto} to show distribution
     * of statuses.
     *
     * @return {@link List} of {@link UserLocationStatisticDto}
     */
    List<UserLocationStatisticDto> getUserLocationsDistribution(String groupBy);

    /**
     * Method to get List of {@link UserEmailPreferencesStatisticDto} to show
     * distribution of preferences by type and periodicity.
     *
     * @return {@link List} of {@link UserEmailPreferencesStatisticDto}
     */
    List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution();

    /**
     * Count total active users in the system.
     */
    Long countActiveUsers();
}
