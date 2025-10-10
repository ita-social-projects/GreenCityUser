package greencity.repository;

import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.dto.user.UserEmailDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Method to find all {@link User} users by emails.
     *
     * @param emails {@link List} of emails to search for
     * @return {@link List} of {@link User} with matching emails
     */
    List<User> findAllByEmailIn(List<String> emails);

    /**
     * Method to find all {@link UserEmailDto} user emails by user ids.
     *
     * @param userIds list of user ids
     * @return list of {@link UserEmailDto} containing information about user's
     *         email
     */
    @Query("""
                SELECT new greencity.dto.user.UserEmailDto(u.id, u.email)
                FROM User u
                WHERE u.id IN :userIds
        """)
    List<UserEmailDto> findAllEmailsByIdIn(List<Long> userIds);

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
     * Find email by id.
     *
     * @param id - User's id
     * @return User's email
     */
    @Query("SELECT email FROM User WHERE id=:id")
    Optional<String> findEmailById(Long id);

    /**
     * Find email by uuid.
     *
     * @param uuid - User's uuid
     * @return User's email
     */
    @Query("SELECT email FROM User WHERE uuid=:uuid")
    Optional<String> findEmailByUuid(String uuid);

    /**
     * Find uuid by email.
     *
     * @param email - User email
     * @return User uuid
     */
    @Query("SELECT uuid FROM User WHERE email=:email")
    Optional<String> findUuidByEmail(String email);

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
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Method that finds user ids by emailPreference and periodicity.
     *
     * @param emailPreference of user.
     * @param periodicity     of notification.
     * @return list of user ids.
     */
    @Query(nativeQuery = true, value = """
            SELECT u.*
            FROM users u
            LEFT JOIN user_email_preferences uep ON u.id = uep.user_id
            WHERE uep.email_preference = :emailPreference AND uep.periodicity = :periodicity
        """)
    List<User> findAllByEmailPreferenceAndEmailPeriodicity(String emailPreference, String periodicity);

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
     * Retrieves the distribution of user roles for active users.
     *
     * @return A list of UserRoleStatisticDto objects containing the role and the
     *         count of users with that role.
     */
    @Query("""
        SELECT new greencity.dto.user.UserRoleStatisticDto(u.role, COUNT(u.id))
        FROM User u
        WHERE u.userStatus = 'VERIFIED'
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
             JOIN uep.user u
             WHERE u.userStatus = 'VERIFIED'
             GROUP BY uep.emailPreference, uep.periodicity
        """)
    List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution();

    /**
     * Counts users grouped by their registration date within a specified date range
     * and granularity.
     *
     * @param startDate   The start date of the range to consider (inclusive).
     * @param endDate     The end date of the range to consider (inclusive).
     * @param granularity The time unit for grouping results
     *                    {@link greencity.enums.DateGranularity}
     * @return A list of tuples containing the date group and the count of users
     *         registered in that group.
     */
    @Query(value = """
            SELECT new greencity.dto.user.UserRegistrationStatisticDto(
                FUNCTION('DATE_TRUNC', :granularity, u.dateOfRegistration) as dateGroup,
                COUNT(u.id))
            FROM User u
            WHERE u.dateOfRegistration >= :startDate
            AND u.dateOfRegistration <= :endDate
            GROUP BY dateGroup
            ORDER BY dateGroup
        """)
    List<UserRegistrationStatisticDto> countUsersByRegistrationDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("granularity") String granularity);

    /**
     * Checks if there is a user with the given uuid.
     *
     * @param uuid the uuid to search for
     * @return true if such a user exists, false otherwise
     */
    boolean existsUserByUuid(String uuid);
}
