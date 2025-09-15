package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.todolist.CustomToDoListItemResponseDto;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.DeactivateUserRequestDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAllFriendsDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfileStatisticsDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.dto.user.UserVOShort;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Provides the interface to manage {UserVO} entity.
 */
public interface UserService {
    /**
     * Updates an employee's rating information using the provided UserRatingDto.
     *
     * @param userRatingDto The UserRatingDto containing the updated rating
     *                      information.
     */
    void updateUserRating(UserAddRatingDto userRatingDto);

    /**
     * Find all {@link UserVOShort} with {@link EmailNotification} type.
     *
     * @param emailNotification - type of {@link EmailNotification}
     * @return list of {@link UserVOShort}
     */
    List<UserVOShort> findAllByEmailNotification(EmailNotification emailNotification);

    /**
     * Delete from the database users that have status 'DEACTIVATED' and last
     * visited the site 2 years ago.
     *
     * @return number of deleted rows.
     */
    int scheduleDeleteDeactivatedUsers();

    /**
     * Find and return city and coordinates .
     *
     * @return {@link UserCityDto}
     **/
    UserCityDto findAllUsersCities(Long userId);

    /**
     * Find and return all registration months. Runs an SQL Query which is described
     * in {@link UserVO} under {@link jakarta.persistence.NamedNativeQuery}
     * annotation. Spring Data JPA can run a named native query that follows the
     * naming convention {entityClass.repositoryMethodName}.
     *
     * @return {@link List} of {@link RegistrationStatisticsDtoResponse}
     **/
    Map<Integer, Long> findAllRegistrationMonthsMap();

    /**
     * Method that allow you to save new {@link UserVO}.
     *
     * @param user a value of {@link UserVO}
     */
    UserVO save(UserVO user);

    /**
     * Method that allow you to find {@link UserVOShort} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVOShort}
     */
    UserVOShort findById(Long id);

    /**
     * Method that allow you to find {@link UserVO} by email.
     *
     * @param email a value of {@link String}
     * @return {@link UserVO} with this email.
     */
    UserVO findByEmail(String email);

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link Optional} of found {@link UserVO}.
     */
    Optional<UserVO> findNotDeactivatedByEmail(String email);

    /**
     * Find UserVO's id by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} id
     */
    Long findIdByEmail(String email);

    /**
     * Find UserVO's uuid by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} uuid
     */
    String findUuIdByEmail(String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto}
     */
    UserRoleDto updateRole(Long id, Role role, String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param userEmail        {@link UserVO} email.
     * @param role             {@link Role} for user.
     * @param currentUserEmail current user email
     * @return {@link UserRoleDto}
     */
    UserRoleDto updateRole(String userEmail, Role role, String currentUserEmail);

    /**
     * Update status of user.
     *
     * @param id         {@link UserVO} id.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, String email);

    /**
     * Update status of user.
     *
     * @param userEmail  {@link UserVO} email.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     */
    UserStatusDto updateStatus(String userEmail, UserStatus userStatus, String currentUserEmail);

    /**
     * Find {@link UserVO}-s by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableDto}.
     */
    PageableDto<UserForListDto> findByPage(Pageable pageable);

    /**
     * Find {@link UserVO} for management by email.
     *
     * @param email user's email.
     * @return a dto of {@link UserManagementDto}.
     */
    UserManagementDto findUserForManagement(String email);

    /**
     * Find {@link UserVO}s for management by page.
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     */
    PageableAdvancedDto<UserManagementDto> findUsersForManagement(Pageable pageable);

    /**
     * Method that allows you to update {@link UserVO} by dto.
     *
     * @param dto - dto {@link UserManagementDto} with updated fields for updating
     *            {@link UserVO}.
     */
    void updateUser(Long userId, UserManagementUpdateDto dto);

    /**
     * Method that allows you to update {@link UserVO} by email in dto.
     *
     * @param dto - dto {@link UserManagementDto} with updated fields for updating
     *            {@link UserVO}.
     */
    void updateUser(UserManagementUpdateDto dto);

    /**
     * The method which return array of user role by user id.
     *
     * @return {@link RoleDto}.
     */
    RoleDto getRoles(Long id);

    /**
     * Get {@link EmailNotification} status for {@link UserVO}.
     *
     * @return user {@link EmailNotification} status.
     */
    EmailNotification getEmailNotificationsStatuses(String email);

    /**
     * Find users by filter.
     *
     * @param filterUserDto contains objects whose values determine the filter
     *                      parameters of the returned list.
     * @param pageable      pageable configuration.
     * @return {@link PageableDto}.
     */
    PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable);

    /**
     * Get {@link UserVO} dto by principal (email).
     *
     * @param email - email of user.
     * @return {@link UserUpdateDto}.
     */
    UserUpdateDto getUserUpdateDtoByEmail(String email);

    /**
     * Update {@link UserVO}.
     *
     * @param dto   {@link UserUpdateDto} - dto with new {@link UserVO} params.
     * @param email {@link String} - email of user that need to update.
     * @return {@link UserVO}.
     */
    UserUpdateDto update(UserUpdateDto dto, String email);

    /**
     * Update ubs employee {@link UserVO}.
     *
     * @param newEmployeeEmail {@link String} - new employee's email.
     * @param uuid             {@link String} - uuid of employee.
     */
    void updateEmployeeEmail(String newEmployeeEmail, String uuid);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id              - user's id
     * @return - number of updated rows
     */
    int updateUserRefreshToken(String refreshTokenKey, Long id);

    /**
     * Method returns list of available (not ACTIVE) customToDoListItem for user.
     *
     * @param userId id of the {@link UserVO} current user.
     * @return List of {@link CustomToDoListItemResponseDto}
     */
    List<CustomToDoListItemResponseDto> getAvailableCustomToDoListItems(Long userId, Long habitID);

    /**
     * Counts all users by user {@link UserStatus} ACTIVATED.
     *
     * @return amount of users with {@link UserStatus} ACTIVATED.
     */
    long getActivatedUsersAmount();

    /**
     * Update user profile picture {@link UserVO}.
     *
     * @param image  {@link MultipartFile}
     * @param email  {@link String} - email of user that need to update.
     * @param base64 {@link String} - picture in base 64 format.
     * @return {@link UserVO}.
     */
    UserVO updateUserProfilePicture(MultipartFile image, String email,
        String base64);

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @param email {@link String} - email of user that need to update.
     */
    void deleteUserProfilePicture(String email);

    /**
     * Save user profile information {@link UserVO}.
     */
    String saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String name);

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId - {@link UserVO}'s id
     */
    boolean checkIfTheUserIsOnline(Long userId);

    /**
     * The method checks by email if a {@link UserVO} is online.
     *
     * @param email - {@link UserVO}'s email
     */
    boolean checkIfTheUserIsOnline(String email);

    /**
     * Method return user profile information {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     */
    UserProfileDtoResponse getUserProfileInformation(Long userId);

    /**
     * Method return user profile statistics {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     */
    UserProfileStatisticsDto getUserProfileStatistics(Long userId);

    /**
     * Get user and six friends with the online status {@link UserVO}.
     *
     * @param userId {@link Long}
     */
    UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId);

    /**
     * Get user and all friends with the online status {@link UserVO} by page.
     *
     * @param userId {@link Long}
     */
    UserAndAllFriendsWithOnlineStatusDto getAllFriendsWithTheOnlineStatus(Long userId, Pageable pageable);

    /**
     * Method deactivates all the {@link UserVO} by list of IDs.
     *
     * @param listId {@link List} of {@link UserVO}s` ids to be deactivated
     * @return {@link List} of {@link UserVO}s` ids
     */
    List<Long> deactivateAllUsers(List<Long> listId);

    /**
     * change {@link UserVO}'s status to ACTIVATE.
     *
     * @param id {@link UserVO}'s id
     */
    UserActivationDto setActivatedStatus(Long id);

    /**
     * Method for getting all Users.
     *
     * @return {@link List} of {@link UserVOShort} instances.
     */
    List<UserVOShort> findAll();

    /**
     * Method that finds users by name.
     *
     * @return {@link List} of {@link UserAllFriendsDto} instances.
     */
    PageableDto<UserAllFriendsDto> findUserByName(String name, Pageable page, Long id);

    /**
     * {@inheritDoc}
     */
    PageableAdvancedDto<UserManagementVO> search(Pageable pageable, UserManagementViewDto userManagementViewDto);

    /**
     * Creates and returns uuid of current user.
     *
     * @param currentUser {@link UserVO} - current user.
     * @return {@link UbsTableCreationDto} - uuid of current user.
     */
    UbsTableCreationDto createUbsRecord(UserVO currentUser);

    /**
     * change {@link UserVO}'s status to DEACTIVATE.
     *
     * @param userVO  {@link UserVO} who send deactivation request.
     * @param uuid    {@link UserVO}'s uuid.
     * @param request {@link DeactivateUserRequestDto} deactivated information.
     */
    UserDeactivationReasonDto deactivateUser(String uuid, DeactivateUserRequestDto request, UserVO userVO);

    /**
     * Method for getting a {@link List} of {@link String} - reasons for
     * deactivation of the current user.
     *
     * @param id        {@link Long} - user's id.
     * @param adminLang {@link String} - current administrator language.
     * @return {@link List} of {@link String}.
     */
    List<String> getDeactivationReason(Long id, String adminLang);

    /**
     * Method that update user language column.
     *
     * @param userId     {@link Long} -current user's id.
     * @param languageId {@link Long} - language id.
     */
    void updateUserLanguage(Long userId, Long languageId);

    /**
     * Method that finds user ids by emailPreference and periodicity.
     *
     * @param emailPreference of user.
     * @param periodicity     of notification.
     * @return list of {@link UserVOShort}.
     */
    List<UserVOShort> findAllByEmailPreferenceAndEmailPeriodicity(EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity);

    /**
     * Method that return UserVo by UUid.
     *
     * @return {@link UserVO}
     */
    UbsCustomerDto findUbsCustomerDtoByUuid(String uuid);

    /**
     * Method that mark User Deactivated.
     */
    void markUserAsDeactivated(String uuid);

    /**
     * Method that mark User Activated.
     */
    void markUserAsActivated(String uuid);

    /**
     * Method find user with admin authority.
     */
    UserVO findAdminById(Long id);

    /**
     * Method checks the existence of the user by uuid.
     *
     * @param uuid {@link String} - for found user.
     * @return {@link Boolean}.
     */
    Boolean checkIfUserExistsByUuid(String uuid);

    /**
     * Method checks the existence of an active user by uuid.
     * 
     * @param uuid user's uuid.
     */
    boolean checkIfActiveUserExistsByUuid(String uuid);

    /**
     * Updates last activity time for a given user by email.
     *
     * @param email                - {@link UserVO}'s email.
     * @param userLastActivityTime - new {@link UserVO}'s last activity time.
     */
    void updateUserLastActivityTimeByEmail(String email, LocalDateTime userLastActivityTime);

    /**
     * Method for checking Users online status (true or false).
     *
     * @param request {@link UsersOnlineStatusRequestDto} - request with current
     *                user ID and list of Users ID whose statuses need to be
     *                checked.
     */
    void checkUsersOnlineStatus(UsersOnlineStatusRequestDto request);

    /**
     * Method for getting user language.
     *
     * @param uuid user uuid.
     * @return user language.
     */
    String findUserLanguageByUuid(String uuid);

    /**
     * Retrieves the list of IDs of all users who have the {@code UserStatus} set to
     * {@code ACTIVATED}. This method is typically used to filter active users for
     * further processing or analysis.
     *
     * @return a list of {@code Long} values representing the IDs of all activated
     *         users
     */
    List<Long> findAllActivatedUserIds(List<Long> ids);

    /**
     * Method that allows you to find not 'DEACTIVATED' {@link UserVOAdvancedDto} by
     * email.
     *
     * @param email - {@link UserVOAdvancedDto}'s email.
     * @return {@link Optional} of found {@link UserVOAdvancedDto}.
     */
    Optional<UserVOAdvancedDto> findNotDeactivatedByEmailAdvanced(String email);

    /**
     * Method that allows you to find {@link UserVOAdvancedDto} by email.
     *
     * @param email - {@link UserVOAdvancedDto}'s email.
     * @return {@link Optional} of found {@link UserVOAdvancedDto}.
     */
    Optional<UserVOAdvancedDto> findByEmailAdvanced(String email);

    /**
     * Method that allows you to create a user on the GreenCity microservice.
     *
     * @param newUserId      - {@link Long} user's id on GreenCityUser.
     * @param profilePicture - {@link String} of user's profilePicture.
     */
    void createGreenCityUser(Long newUserId, String profilePicture);

    /**
     * Method that allows to find not 'DEACTIVATED' {@link UserVOShort} by email.
     *
     * @param email - user's email
     * @return {@link Optional} of found {@link UserVOShort}.
     */
    Optional<UserVOShort> findNotDeactivatedByEmailReduced(String email);

    /**
     * Method to find all {@link UserVO} users by emails.
     *
     * @param emails {@link List} of emails to search for
     * @return {@link List} of {@link UserVO} with matching emails
     */
    List<UserVO> findAllByEmailIn(List<String> emails);

    /**
     * Method to find all emails by ids.
     *
     * @param ids {@link List} of ids to search for
     * @return {@link List} of emails
     */
    List<String> findAllEmailsByIdIn(List<Long> ids);
}
