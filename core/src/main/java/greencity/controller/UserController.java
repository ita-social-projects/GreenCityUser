package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.annotations.CurrentUserId;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidBase64;
import greencity.client.GreenCityRemoteClient;
import greencity.constant.HttpStatuses;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.language.LanguageVO;
import greencity.dto.position.PositionAuthoritiesDto;
import greencity.dto.todolist.CustomToDoListItemResponseDto;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAllFriendsDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfileStatisticsDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.dto.user.DeactivateUserRequestDto;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.DateGranularity;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.security.service.AuthorityService;
import greencity.security.service.PositionService;
import greencity.service.EmailService;
import greencity.service.ManagementUserStatisticsService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
    private final ManagementUserStatisticsService managementUserStatisticsService;
    private final EmailService emailService;
    private final PositionService positionService;
    private final AuthorityService authorityService;
    private final GreenCityRemoteClient greenCityRemoteClient;

    /**
     * The method which update user status. Parameter principal are ignored because
     * Spring automatically provide the Principal object.
     *
     * @param userStatusDto - dto with updated filed.
     * @return {@link UserStatusDto}
     */
    @Operation(summary = "Update status of user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserStatus.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("status")
    public ResponseEntity<UserStatusDto> updateStatus(
        @Valid @RequestBody UserStatusDto userStatusDto, Principal principal) {
        return ResponseEntity.ok().body(userService.updateStatus(
            userStatusDto.getId(), userStatusDto.getUserStatus(), principal.getName()));
    }

    /**
     * The method which update user role. Parameter principal are ignored because
     * Spring automatically provide the Principal object.
     *
     * @param id   of updated user
     * @param body contains new role
     * @return {@link UserRoleDto}
     */
    @Operation(summary = "Update role of user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserRoleDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("{id}/role")
    public ResponseEntity<UserRoleDto> updateRole(
        @PathVariable Long id,
        @NotNull @RequestBody Map<String, String> body,
        Principal principal) {
        Role role = Role.valueOf(body.get("role"));
        UserRoleDto userRoleDto = new UserRoleDto(id, role);
        return ResponseEntity.ok().body(userService.updateRole(
            userRoleDto.getId(), userRoleDto.getRole(), principal.getName()));
    }

    /**
     * The method which return list of users by page. Parameter pageable ignored
     * because swagger ui shows the wrong params, instead they are explained in the
     * {@link ApiPageable}.
     *
     * @param pageable - pageable configuration.
     * @return list of {@link PageableDto}
     */
    @Operation(summary = "Get users by page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = PageableDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("all")
    public ResponseEntity<PageableDto<UserForListDto>> getAllUsers(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok().body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of user role by user id.
     *
     * @return {@link RoleDto}
     */
    @Operation(summary = "Get user role by user id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = RoleDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("roles")
    public ResponseEntity<RoleDto> getRoles(@RequestParam Long id) {
        return ResponseEntity.ok().body(userService.getRoles(id));
    }

    /**
     * The method which return email status authorization
     * user{@link EmailNotification}.
     *
     * @return {@link EmailNotification} array
     */
    @Operation(summary = "Get email notifications status by authorization user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EmailNotification[].class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("emailNotifications")
    public ResponseEntity<List<EmailNotification>> getEmailNotifications(Principal principal) {
        return ResponseEntity.ok().body(List.of(userService.getEmailNotificationsStatuses(principal.getName())));
    }

    /**
     * The method which return list of users by filter. Parameter pageable ignored
     * because swagger ui shows the wrong params, instead they are explained in the
     * {@link ApiPageable}.
     *
     * @param filterUserDto dto which contains fields with filter criteria.
     * @param pageable      - pageable configuration.
     * @return {@link PageableDto}
     */
    @Operation(summary = "Filter all user by search criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = PageableDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @PostMapping("filter")
    public ResponseEntity<PageableDto<UserForListDto>> getUsersByFilter(
        @Parameter(hidden = true) Pageable pageable, @RequestBody FilterUserDto filterUserDto) {
        return ResponseEntity.ok().body(userService.getUsersByFilter(filterUserDto, pageable));
    }

    /**
     * Get {@link UserVO} dto by principal (email) from access token.
     *
     * @return {@link UserUpdateDto}.
     */
    @Operation(summary = "Get User dto by principal (email) from access token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserUpdateDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<UserUpdateDto> getUserByPrincipal(Principal principal) {
        return ResponseEntity.ok().body(userService.getUserUpdateDtoByEmail(principal.getName()));
    }

    /**
     * Update {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Update User")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping
    public ResponseEntity<UserUpdateDto> updateUser(@Valid @RequestBody UserUpdateDto dto, Principal principal) {
        return ResponseEntity.ok().body(userService.update(dto, principal.getName()));
    }

    /**
     * Update ubs employee's email {@link UserVO}.
     */
    @Operation(summary = "Update employee's email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PutMapping("/employee-email")
    public ResponseEntity<HttpStatus> updateEmployeeEmail(@RequestParam String newEmployeeEmail,
        @RequestParam String uuid) {
        userService.updateEmployeeEmail(newEmployeeEmail, uuid);
        return ResponseEntity.ok().build();
    }

    /**
     * Method returns list of available (not ACTIVE) custom to-do list items for
     * user.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Get available custom to-do list items for current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/{userId}/{habitId}/custom-to-do-list-items/available")
    public ResponseEntity<List<CustomToDoListItemResponseDto>> getAvailableCustomToDoListItems(
        @Parameter(description = "Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @PathVariable Long habitId) {
        return ResponseEntity.ok().body(userService.getAvailableCustomToDoListItems(userId, habitId));
    }

    /**
     * Counts all users by user {@link UserStatus} ACTIVATED.
     *
     * @return amount of users with {@link UserStatus} ACTIVATED.
     */
    @Operation(summary = "Get all activated users amount")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/activatedUsersAmount")
    public ResponseEntity<Long> getActivatedUsersAmount() {
        return ResponseEntity.ok().body(userService.getActivatedUsersAmount());
    }

    /**
     * Update user profile picture {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Update user profile picture")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        path = "/profilePicture")
    public ResponseEntity<HttpStatus> updateUserProfilePicture(
        @Parameter(description = "pass image as base64") @ValidBase64 @RequestPart(required = false) String base64,
        @Parameter(description = "Profile picture") @ImageValidation @RequestPart(required = false) MultipartFile image,
        Principal principal) {
        userService.updateUserProfilePicture(image, principal.getName(), base64);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Delete user profile picture")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @DeleteMapping(path = "/deleteProfilePicture")
    public ResponseEntity<HttpStatus> deleteUserProfilePicture(Principal principal) {
        userService.deleteUserProfilePicture(principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Method for save user profile information {@link UserProfileDtoResponse}.
     *
     * @param userProfileDtoRequest - dto for {@link UserVO} entity.
     * @return dto {@link UserProfileDtoResponse} instance.
     */
    @Operation(summary = "Save user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping(path = "/profile")
    public ResponseEntity<String> save(
        @Parameter(required = true) @RequestBody @Valid UserProfileDtoRequest userProfileDtoRequest,
        Principal principal) {
        return ResponseEntity.ok().body(userService.saveUserProfile(userProfileDtoRequest, principal.getName()));
    }

    /**
     * Method returns user profile information.
     *
     * @return {@link UserProfileDtoResponse}.
     */
    @Operation(summary = "Get user profile information by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/profile/")
    public ResponseEntity<UserProfileDtoResponse> getUserProfileInformation(
        @Parameter(description = "Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.ok().body(userService.getUserProfileInformation(userId));
    }

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Check by id if the user is online")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("isOnline/{userId}/")
    public ResponseEntity<Boolean> checkIfTheUserIsOnline(
        @Parameter(description = "Id of the user. Cannot be empty.") @PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.checkIfTheUserIsOnline(userId));
    }

    /**
     * Method returns user profile statistics.
     *
     * @return {@link UserProfileStatisticsDto}.
     */
    @Operation(summary = "Get user profile statistics by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/{userId}/profileStatistics/")
    public ResponseEntity<UserProfileStatisticsDto> getUserProfileStatistics(
        @Parameter(description = "Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.ok().body(userService.getUserProfileStatistics(userId));
    }

    /**
     * The method get {@link UserVO}s with online status for the current user-id.
     *
     * @return {@link UserAndFriendsWithOnlineStatusDto}.
     */
    @MessageMapping("/userAndSixFriendsWithOnlineStatus")
    @SendTo("/topic/sixUsersOnlineStatus")
    public UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId) {
        return userService.getUserAndSixFriendsWithOnlineStatus(userId);
    }

    /**
     * The method get all {@link UserVO}s with online status for the current
     * user-id.
     *
     * @return {@link UserAndAllFriendsWithOnlineStatusDto}.
     */
    @MessageMapping("/userAndAllFriendsWithOnlineStatus")
    @SendTo("/topic/userAndAllFriendsOnlineStatus")
    public UserAndAllFriendsWithOnlineStatusDto getUserAndAllFriendsWithOnlineStatus(Long userId, Pageable pageable) {
        return userService.getAllFriendsWithTheOnlineStatus(userId, pageable);
    }

    /**
     * Method for checking Users online status (true or false).
     *
     * @param request {@link UsersOnlineStatusRequestDto} - request with current
     *                User ID and list of Users ID whose statuses need to be
     *                checked.
     */
    @MessageMapping("/usersOnlineStatus")
    public void checkUsersOnlineStatus(@Payload UsersOnlineStatusRequestDto request) {
        userService.checkUsersOnlineStatus(request);
    }

    /**
     * Method find user by principal.
     *
     * @return {@link ResponseEntity}.
     */
    @Operation(summary = "Find current user by principal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findByEmail")
    public ResponseEntity<UserVO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.findByEmail(email));
    }

    /**
     * Get {@link UserVO} by id.
     *
     * @return {@link UserUpdateDto}.
     */
    @Operation(summary = "Get User by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findById")
    public ResponseEntity<UserVO> findById(@RequestParam Long id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    /**
     * Method that allow you to find {@link UserVO} by Id.
     *
     * @return {@link UserUpdateDto}.
     */
    @Operation(summary = "Get User by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findByIdForAchievement")
    public ResponseEntity<UserVOAchievement> findUserForAchievement(@RequestParam Long id) {
        return ResponseEntity.ok().body(userService.findUserForAchievement(id));
    }

    /**
     * Method that allow you to find {@link UserVO} for management.
     *
     * @return {@link UserUpdateDto}.
     */
    @Operation(summary = "Get User for management")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/findUserForManagement")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<UserManagementDto>> findUserForManagementByPage(
        @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok().body(userService.findUserForManagementByPage(pageable));
    }

    /**
     * Method that allow you to find {@link UserVO} by Id.
     *
     * @return {@link UserUpdateDto}.
     */
    @Operation(summary = "Get User by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/searchBy")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<UserManagementDto>> searchBy(
        @RequestParam(required = false, name = "query") String query,
        @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok().body(userService.searchBy(pageable, query));
    }

    /**
     * Method that updates user data.
     *
     * @param userDto dto with updated fields.
     */
    @Operation(summary = "update via UserManagement")
    @ApiResponses(value = @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserManagement(
        @PathVariable @NotNull Long id,
        @RequestBody UserManagementUpdateDto userDto) {
        userService.updateUser(id, userDto);
    }

    /**
     * Method that allow you to find all users {@link UserVO}.
     *
     * @return {@link UserVO list}.
     */
    @Operation(summary = "Get all Users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<UserVO>> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link UserVO}.
     */
    @Operation(summary = "Get find not 'DEACTIVATED' User by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findNotDeactivatedByEmail")
    public ResponseEntity<UserVO> findNotDeactivatedByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.findNotDeactivatedByEmail(email).orElse(null));
    }

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by id.
     *
     * @param id - {@link UserVO}'s id
     * @return {@link UserVO}.
     */
    @Operation(summary = "Get find not 'DEACTIVATED' User by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findNotDeactivatedById")
    public ResponseEntity<UserVO> findNotDeactivatedById(@RequestParam Long id) {
        return ResponseEntity.ok().body(userService.findNotDeactivatedById(id).orElse(null));
    }

    /**
     * Method creates record in ubs table.
     *
     * @return {@link UbsTableCreationDto}
     */
    @Parameter(hidden = true)
    @Operation(summary = "Creates uuid and returns it to ubs microservice.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/createUbsRecord")
    public ResponseEntity<UbsTableCreationDto> createUbsRecord(
        @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(userService.createUbsRecord(userVO));
    }

    /**
     * Get {@link UserVO} id by email.
     *
     * @return {@link Long}.
     */
    @Operation(summary = "Get User id by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/findIdByEmail")
    public ResponseEntity<Long> findIdByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.findIdByEmail(email));
    }

    /**
     * Get {@link UserVO} uuid by email.
     *
     * @return {@link String}.
     */
    @Operation(summary = "Get User uuid by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/findUuidByEmail")
    public ResponseEntity<String> findUuidByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.findUuIdByEmail(email));
    }

    /**
     * Method for deactivating a {@link UserVO} by setting its status to
     * DEACTIVATED, preventing the user from logging into the system.
     *
     * @param userVO  the {@link UserVO} object representing the current user
     *                performing the deactivation.
     * @param uuid    the UUID of the user to deactivate.
     * @param request the {@link DeactivateUserRequestDto} containing deactivation
     *                information.
     * @return ResponseEntity indicating the success of the deactivation operation.
     */
    @Operation(summary = "Deactivate user indicating the reason for deactivation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/deactivate")
    public ResponseEntity<ResponseEntity.BodyBuilder> deactivateUser(
        @Parameter(hidden = true) @CurrentUser UserVO userVO,
        @RequestParam String uuid,
        @Valid @RequestBody DeactivateUserRequestDto request) {
        UserDeactivationReasonDto userDeactivationDto = userService.deactivateUser(uuid, request, userVO);
        emailService.sendReasonOfDeactivation(userDeactivationDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for getting {@link String} user language.
     *
     * @param userVO {@link UserVO} the current user that wants to get his profile
     *               language
     * @return current user language {@link String}.
     */
    @Operation(summary = "Get the current User language")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/lang")
    public ResponseEntity<String> getUserLang(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(userVO.getLanguageVO().getCode());
    }

    /**
     * Method for getting user language.
     *
     * @param uuid uuid of user.
     * @return user language.
     */
    @Operation(summary = "Get the current User language")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/findUserLanguageByUuid")
    public ResponseEntity<String> findUserLanguageByUuid(@RequestParam String uuid) {
        return ResponseEntity.ok().body(userService.findUserLanguageByUuid(uuid));
    }

    /**
     * Method for getting a {@link List} of {@link String} - reasons for
     * deactivation of the current user.
     *
     * @param id        {@link Long} - user's id.
     * @param adminLang {@link String} - current administrator language.
     * @return {@link List} of {@link String} - reasons for deactivation of the
     *         current user.
     */
    @Operation(summary = "Get list reasons of deactivating the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/reasons")
    public ResponseEntity<List<String>> getReasonsOfDeactivation(
        @RequestParam("id") Long id, @RequestParam("admin") String adminLang) {
        return ResponseEntity.ok().body(userService.getDeactivationReason(id, adminLang));
    }

    /**
     * Method that change user language.
     *
     * @param userVO     {@link UserVO} the current user that wants to change his
     *                   profile language
     * @param languageId {@link Long} language id.
     */
    @Operation(summary = "Update user language")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/language/{languageId}")
    public ResponseEntity<Object> setUserLanguage(@Parameter(hidden = true) @CurrentUser UserVO userVO,
        @PathVariable Long languageId) {
        userService.updateUserLanguage(userVO.getId(), languageId);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for setting {@link UserVO}'s status to ACTIVATED.
     *
     * @param id of the searched {@link UserVO}.
     */
    @Operation(summary = "Activate User")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/activate")
    public ResponseEntity<Object> activateUser(@RequestParam Long id) {
        UserActivationDto userActivationDto = userService.setActivatedStatus(id);
        emailService.sendMessageOfActivation(userActivationDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for setting to a list of {@link UserVO} status DEACTIVATED, so the
     * users will not be able to log in into the system.
     *
     * @param listId {@link List} populated with ids of {@link UserVO} to be
     *               deleted.
     */
    @Operation(summary = "Deactivate all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PutMapping("/deactivateAll")
    public ResponseEntity<List<Long>> deactivateAllUsers(@RequestBody List<Long> listId) {
        return ResponseEntity.ok().body(userService.deactivateAllUsers(listId));
    }

    /**
     * Method that allow to search users by several values.
     *
     * @param pageable    {@link Pageable}
     * @param userViewDto {@link UserManagementViewDto} - stores values.
     */
    @Operation(summary = "Search Users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/search")
    public ResponseEntity<PageableAdvancedDto<UserManagementVO>> search(@Parameter(hidden = true) Pageable pageable,
        @RequestBody UserManagementViewDto userViewDto) {
        return ResponseEntity.ok().body(userService.search(pageable, userViewDto));
    }

    /**
     * Method that allow search users by their email notification.
     *
     * @param emailNotification enum with notification value.
     * @return {@link List} of {@link UserVO}
     */
    @Operation(summary = "Search Users by email notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/findAllByEmailNotification")
    public ResponseEntity<List<UserVO>> findAllByEmailNotification(@RequestParam EmailNotification emailNotification) {
        return ResponseEntity.ok().body(userService.findAllByEmailNotification(emailNotification));
    }

    /**
     * Delete from the database users that have status 'DEACTIVATED' and last
     * visited the site 2 years ago.
     *
     * @return number of deleted rows
     */
    @Operation(summary = "Delete deactivated Users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @PostMapping("/deleteDeactivatedUsers")
    public ResponseEntity<Integer> scheduleDeleteDeactivatedUsers() {
        return ResponseEntity.ok().body(userService.scheduleDeleteDeactivatedUsers());
    }

    /**
     * Method that find all users' cities.
     *
     * @return {@link List} of cities
     */
    @Operation(summary = "Find all users cities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)})
    @GetMapping("/findAllUsersCities")
    public ResponseEntity<UserCityDto> findAllUsersCities(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(userService.findAllUsersCities(userVO.getId()));
    }

    /**
     * Method that find all registration months.
     *
     * @return {@link Map} with months
     */
    @Operation(summary = "Find registration months")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/findAllRegistrationMonthsMap")
    public ResponseEntity<Map<Integer, Long>> findAllRegistrationMonthsMap() {
        return ResponseEntity.ok().body(userService.findAllRegistrationMonthsMap());
    }

    /**
     * Method search users by name.
     */
    @Operation(summary = "Search users by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/findUserByName")
    @ApiPageable
    public ResponseEntity<PageableDto<UserAllFriendsDto>> findUserByName(
        @Parameter(hidden = true) Pageable page,
        @RequestParam String name,
        @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(userService.findUserByName(name, page, userVO.getId()));
    }

    /**
     * Get {@link UbsCustomerDto} by uuid.
     *
     * @return {@link UbsCustomerDto}.
     */
    @Operation(summary = "Get User by Uuid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findByUuId")
    public ResponseEntity<UbsCustomerDto> findByUuId(@RequestParam String uuid) {
        return ResponseEntity.ok().body(userService.findUbsCustomerDtoByUuid(uuid));
    }

    /**
     * Check the existence of the user by uuid.
     *
     * @param uuid {@link String} - for found user.
     * @return {@link Boolean}.
     */
    @Operation(summary = "Check the existence of the user by uuid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/checkByUuid")
    public ResponseEntity<Boolean> checkIfUserExistsByUuId(@RequestParam String uuid) {
        return ResponseEntity.ok().body(userService.checkIfUserExistsByUuid(uuid));
    }

    /**
     * Method for mark user like DEACTIVATED .
     *
     * @param uuid - for found user.
     */
    @Operation(summary = "mark user as DEACTIVATED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/markUserAsDeactivated")
    public ResponseEntity<Object> markUserAsDeactivated(
        @RequestParam @Parameter(hidden = true) String uuid) {
        userService.markUserAsDeactivated(uuid);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for mark user like ACTIVATED .
     *
     * @param uuid - for found user.
     */
    @Operation(summary = "mark user as ACTIVATED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/markUserAsActivated")
    public ResponseEntity<Object> markUserAsActivated(
        @RequestParam @Parameter(hidden = true) String uuid) {
        userService.markUserAsActivated(uuid);
        return ResponseEntity.ok().build();
    }

    /**
     * Controller to get information about all employee's authorities.
     *
     * @return @return Set of {@link String}
     */
    @Operation(summary = "Get information about all employee's authorities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/get-all-authorities")
    public ResponseEntity<Object> getAllAuthorities(@RequestParam String email) {
        Set<String> authorities = authorityService.getAllEmployeesAuthorities(email);
        return ResponseEntity.ok().body(authorities);
    }

    /**
     * Controller to get an employee`s positions and all possible related
     * authorities to these positions.
     *
     * @param email {@link String} - employee email.
     * @return {@link PositionAuthoritiesDto}
     */
    @Operation(summary = """
        Get information about an employee`s positions and all possible \
        related authorities to these positions.\
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/get-positions-authorities")
    public ResponseEntity<PositionAuthoritiesDto> getPositionsAndRelatedAuthorities(@RequestParam String email) {
        return ResponseEntity.ok().body(positionService.getPositionsAndRelatedAuthorities(email));
    }

    /**
     * Controller edit an employee`s authorities.
     *
     * @return {@link UserEmployeeAuthorityDto}
     */
    @Operation(summary = "Edit an employee`s authorities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserEmployeeAuthorityDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/edit-authorities")
    public ResponseEntity<Object> editAuthorities(@Valid @RequestBody UserEmployeeAuthorityDto dto) {
        authorityService.updateEmployeesAuthorities(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Controller that update an employee`s authorities to related positions.
     *
     * @param dto - UpdateEmployeeAuthoritiesDto.
     * @return {@link HttpStatus} - Http status code.
     */
    @Operation(summary = "Update an employee`s authorities to related positions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/authorities")
    public ResponseEntity<HttpStatus> updateAuthoritiesToRelatedPositions(
        @Valid @RequestBody EmployeePositionsDto dto) {
        authorityService.updateAuthoritiesToRelatedPositions(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Controller that deactivate employee by uuid.
     *
     * @param uuid - uuid of Employee.
     */
    @Operation(summary = "Deactivate employee by uuid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/deactivate-employee")
    public ResponseEntity<HttpStatus> deactivateEmployee(@RequestParam String uuid) {
        userService.markUserAsDeactivated(uuid);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates an employee's rating information.
     *
     * @param userAddRatingDto The UserRatingDto containing the updated rating
     *                         information.
     * @return A ResponseEntity with HTTP status indicating the success of the
     *         update operation.
     */
    @Operation(summary = "Update an employee's rating information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/user-rating")
    public ResponseEntity<HttpStatus> updateUserRating(@Valid @RequestBody UserAddRatingDto userAddRatingDto) {
        userService.updateUserRating(userAddRatingDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Get user roles distribution.
     *
     * @return {@link List} of {@link UserRoleStatisticDto}.
     */
    @Operation(summary = "Get user roles distribution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/roles-distribution")
    public ResponseEntity<List<UserRoleStatisticDto>> getUserRolesDistribution() {
        return ResponseEntity.ok().body(managementUserStatisticsService.getUserRolesDistribution());
    }

    /**
     * Get user statuses distribution.
     *
     * @return {@link List} of {@link UserStatusStatisticDto}.
     */
    @Operation(summary = "Get user statuses distribution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/statuses-distribution")
    public ResponseEntity<List<UserStatusStatisticDto>> getUserStatusesDistribution() {
        return ResponseEntity.ok().body(managementUserStatisticsService.getUserStatusesDistribution());
    }

    /**
     * Get user email preferences distribution.
     *
     * @return {@link List} of {@link UserEmailPreferencesStatisticDto}.
     */
    @Operation(summary = "Get user email preferences distribution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/email-preferences-distribution")
    public ResponseEntity<List<UserEmailPreferencesStatisticDto>> getUserEmailPreferencesDistribution() {
        return ResponseEntity.ok().body(managementUserStatisticsService.getUserEmailPreferencesDistribution());
    }

    /**
     * Count total active users in the system.
     *
     * @return number of active users in the system
     */
    @Operation(summary = "Get user email preferences distribution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/count-active-users")
    public ResponseEntity<Long> countActiveUsers() {
        return ResponseEntity.ok(managementUserStatisticsService.countActiveUsers());
    }

    /**
     * Find users by email preference and email periodicity.
     *
     * @param emailPreference user's email preference.
     * @param periodicity     email periodicity.
     * @return list of {@link UserVO}
     */
    @Operation(summary = "Find users by email preference and email periodicity.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/email")
    public ResponseEntity<List<UserVO>> findAllByEmailPreferenceAndEmailPeriodicity(
        @RequestParam("email-preference") String emailPreference,
        @RequestParam("email-periodicity") String periodicity) {
        return ResponseEntity.ok(userService.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference, periodicity));
    }

    /**
     * Method to get list of dates and counts of registered users.
     *
     * @param startDate   {@code LocalDateTime} startDate.
     * @param endDate     {@code LocalDateTime} endDate.
     * @param granularity {@link DateGranularity} (eg. day, week, month, year).
     * @return {@link List} of {@link UserRegistrationStatisticDto}.
     */
    @Operation(summary = "Get list of dates and counts of registered users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/registration-statistics")
    public ResponseEntity<List<UserRegistrationStatisticDto>> getUserRegistrationsByDateRange(
        @RequestParam("start-date") LocalDateTime startDate,
        @RequestParam("end-date") LocalDateTime endDate,
        @RequestParam("granularity") DateGranularity granularity) {
        return ResponseEntity
            .ok(managementUserStatisticsService.getUserRegistrationsByDateRange(startDate, endDate, granularity));
    }

    /**
     * Endpoint for retrieving IDs of all users with status {@code ACTIVATED}.
     *
     * @param ids {@link List} of ids to search for users in this range, not
     *            required: if not specified - then the search is done through all
     *            existing users.
     * @return a {@link ResponseEntity} containing a list of activated user IDs
     */
    @Operation(summary = "Get the list of activated user ids")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/activated-ids")
    public ResponseEntity<List<Long>> getActivatedUsersIds(
        @RequestParam(value = "ids", required = false) List<Long> ids) {
        return ResponseEntity.ok(userService.findAllActivatedUserIds(ids));
    }

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVOAdvancedDto} by
     * id.
     *
     * @param id - {@link UserVOAdvancedDto}'s id
     * @return {@link UserVOAdvancedDto}.
     */
    @Operation(summary = "Get find not 'DEACTIVATED' User by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/findNotDeactivatedByIdAdvanced")
    public ResponseEntity<UserVOAdvancedDto> findNotDeactivatedByIdAdvanced(@RequestParam Long id) {
        return ResponseEntity.ok().body(userService.findNotDeactivatedByIdAdvanced(id).orElse(null));
    }
}
