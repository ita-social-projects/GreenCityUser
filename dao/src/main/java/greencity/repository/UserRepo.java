package greencity.repository;

import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.NamedNativeQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides an interface to manage {@link User} entity.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find {@link User} by page.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Find uuid by email.
     *
     * @param email - User email
     * @return User uuid
     */
    @Query("SELECT uuid FROM User WHERE email=:email")
    Optional<String> findUuidByEmail(String email);

    /**
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find not 'DEACTIVATED' {@link User} by id.
     *
     * @param id - {@link User}'s id
     * @return found {@link User}
     */
    @Query("FROM User WHERE id=:id AND userStatus <> 1")
    Optional<User> findNotDeactivatedById(Long id);

    /**
     * Find all {@link User}'s with {@link EmailNotification} type.
     *
     * @param emailNotification - type of {@link EmailNotification}
     * @return list of {@link User}'s
     */
    List<User> findAllByEmailNotification(EmailNotification emailNotification);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id              - user's id
     * @return - number of updated rows
     */
    @Modifying
    @Query(value = "UPDATE User SET refreshTokenKey=:refreshTokenKey WHERE id=:id")
    int updateUserRefreshToken(String refreshTokenKey, Long id);

    /**
     * Counts all users by user {@link UserStatus}.
     *
     * @return amount of user with given {@link UserStatus}.
     */
    long countAllByUserStatus(UserStatus userStatus);

    /**
     * Get all user friends{@link User}.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM users WHERE users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')\
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));\
        """)
    List<User> getAllUserFriends(Long userId);

    /**
     * Get all user friends{@link User}. by page.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM users WHERE users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND') \
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'))\
        """)
    Page<User> getAllUserFriends(Long userId, Pageable pageable);

    /**
     * Get six friends with the highest rating {@link User}.
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM users WHERE users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND') \
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND')) \
        ORDER BY users.rating DESC LIMIT 6;\
        """)
    List<User> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Delete from the database users that have status_user 'DEACTIVATED' and last
     * visited the site 2 years ago.
     *
     * @return number of deleted rows
     **/
    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM users where status = 1 \
        AND last_activity_time + interval '2 year' <= CURRENT_TIMESTAMP\
        """)
    int scheduleDeleteDeactivatedUsers();

    /**
     * Set {@link User}s' statuses to 'DEACTIVATED'.
     *
     * @param ids - {@link List} of ids of {@link User} to be 'DEACTIVATED'
     **/
    @Modifying
    @Query(value = "UPDATE User SET userStatus = 1 where id IN(:ids)")
    void deactivateSelectedUsers(List<Long> ids);

    /**
     * Method returns {@link User} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link User}.
     */
    @Query("""
        SELECT u FROM User u WHERE CONCAT(u.id,'') LIKE LOWER(CONCAT('%', :query, '%')) \
        OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))\
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) \
        OR LOWER(u.userCredo) LIKE LOWER(CONCAT('%', :query, '%'))\
        """)
    Page<User> searchBy(Pageable paging, String query);

    /**
     * Find and return all registration months. Runs an SQL Query which is described
     * in {@link User} under {@link NamedNativeQuery} annotation. Spring Data JPA
     * can run a named native query that follows the naming convention
     * {entityClass.repositoryMethodName}.
     *
     * @return {@link List} of {@link RegistrationStatisticsDtoResponse}
     **/
    @Query(nativeQuery = true)
    List<RegistrationStatisticsDtoResponse> findAllRegistrationMonths();

    /**
     * Converts result of findAllRegistrationMonths() method to {@link Map}.
     *
     * @return {@link Map}
     */
    default Map<Integer, Long> findAllRegistrationMonthsMap() {
        return findAllRegistrationMonths().stream().collect(
            Collectors.toMap(RegistrationStatisticsDtoResponse::getMonth, RegistrationStatisticsDtoResponse::getCount));
    }

    /**
     * Method that finds user.
     *
     * @param id {@link Long} -current user's id.
     * @return {@link User}.
     */
    @Query(value = "select u from User u where u.id = :id")
    Optional<User> findUserForAchievement(Long id);

    /**
     * Find id by UUid.
     *
     * @param uuid - User uuid
     * @return User
     */
    Optional<User> findUserByUuid(String uuid);

    /**
     * Method that finds all users by name.
     */
    @Query(nativeQuery = true, value = """
        select * from users u where u.id <> :userId and
         LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))\
        """)
    Page<User> findAllUsersByName(String name, Pageable page, Long userId);

    /**
     * Method that checks if the email user exists.
     *
     * @param email - email of User.
     * @return - return true if User exists and false if not.
     */
    boolean existsUserByEmail(String email);

    /**
     * Updates last activity time for a given user by email.
     *
     * @param email                - {@link User}'s email.
     * @param userLastActivityTime - new {@link User}'s last activity time.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET lastActivityTime=:userLastActivityTime WHERE email=:email")
    void updateUserLastActivityTimeByEmail(String email, LocalDateTime userLastActivityTime);

    /**
     * Method to get all User's by users IDs.
     *
     * @param usersId {@link Long} - list of users IDs.
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users where users.id in (:usersId)")
    List<User> getAllUsersByUsersId(List<Long> usersId);
    
    /**
     * Find all {@link UserManagementVO}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link UserManagementVO}
     */
    @Query(" SELECT new greencity.dto.user.UserManagementVO(u.id, u.name, u.email, u.userCredo, u.role, u.userStatus) " + " FROM User u ")
    Page<UserManagementVO> findAllManagementVo(Specification<User> filter, Pageable pageable);

    /**
     * Retrieves the distribution of user roles for active users.
     *
     * @return A list of UserRoleStatisticDto objects containing the role and the
     *         count of users with that role.
     */
    @Query("""
     SELECT new greencity.dto.user.UserRoleStatisticDto(u.role, COUNT(u.id))
     FROM User u
     WHERE u.userStatus = 2
     GROUP BY u.role
     """)
    List<UserRoleStatisticDto> getUserRolesDistribution();

    /**
     * Retrieves the distribution of user statuses across all users.
     *
     * @return A list of UserStatusStatisticDto objects containing the status and
     *         the count of users with that status.
     */
    @Query("""
       SELECT new greencity.dto.user.UserStatusStatisticDto(u.userStatus, COUNT(u.id))
       FROM User u
       GROUP BY u.userStatus
       """)
    List<UserStatusStatisticDto> getUserStatusesDistribution();

    /**
     * Retrieves the distribution of users by city.
     *
     * @return A list of UserLocationStatisticDto objects containing the city name
     *         and the count of users in that city.
     */
    @Query("""
       SELECT new greencity.dto.user.UserLocationStatisticDto(
              COALESCE(ul.cityEn, 'No Location'), COUNT(u.id))
       FROM User u
       LEFT JOIN u.userLocation ul
       WHERE u.userStatus = 2
       GROUP BY ul.cityEn
       """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByCity();

    /**
     * Retrieves the distribution of users by region.
     *
     * @return A list of UserLocationStatisticDto objects containing the region name
     *         and the count of users in that region.
     */
    @Query("""
       SELECT new greencity.dto.user.UserLocationStatisticDto(
              COALESCE(ul.regionEn, 'No Location'), COUNT(u.id))
       FROM User u
       LEFT JOIN u.userLocation ul
       WHERE u.userStatus = 2
       GROUP BY ul.regionEn
       """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByRegion();

    /**
     * Retrieves the distribution of users by country.
     *
     * @return A list of UserLocationStatisticDto objects containing the country
     *         name and the count of users in that country.
     */
    @Query("""
       SELECT new greencity.dto.user.UserLocationStatisticDto(
              COALESCE(ul.countryEn, 'No Location'), COUNT(u.id))
       FROM User u
       LEFT JOIN u.userLocation ul
       WHERE u.userStatus = 2
       GROUP BY ul.countryEn
       """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByCountry();

    /**
     * Retrieves the distribution of user email preferences and their periodicity.
     *
     * @return A list of UserEmailPreferencesStatisticDto objects containing the
     *         email preference, periodicity, and the count of users with that
     *         combination.
     */
    @Query("""
            SELECT new greencity.dto.user.UserEmailPreferencesStatisticDto(
                uep.emailPreference, uep.periodicity, COUNT(uep.id)
            )
            FROM UserNotificationPreference uep
            LEFT JOIN User u
            WHERE u.userStatus = 2
            GROUP BY uep.emailPreference, uep.periodicity
       """)
    List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution();

    /**
     * Count total active users in the system.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userStatus IN (greencity.enums.UserStatus.ACTIVATED) ")
    Long countActiveUsers();
}
