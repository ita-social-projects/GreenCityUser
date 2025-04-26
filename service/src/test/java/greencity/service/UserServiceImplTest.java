package greencity.service;

import com.google.maps.model.AddressType;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.GreenCityRemoteClient;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.constant.UpdateConstants;
import greencity.dto.CoordinatesDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.language.LanguageVO;
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
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserWithOnlineStatusDto;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.entity.User;
import greencity.entity.UserDeactivationReason;
import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import static greencity.enums.Role.ROLE_USER;
import static greencity.enums.Role.ROLE_ADMIN;
import static greencity.enums.Role.ROLE_MODERATOR;
import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.DEACTIVATED;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserDeactivationException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.filters.UserSpecification;
import greencity.repository.UserDeactivationRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.ServiceUnavailableException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static greencity.ModelUtils.CREATE_USER_ALL_FRIENDS_DTO;
import static greencity.ModelUtils.TEST_ADMIN;
import static greencity.ModelUtils.TEST_USER;
import static greencity.ModelUtils.TEST_USER_VO;
import static greencity.ModelUtils.getUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    RestClient restClient;

    @Mock
    UserDeactivationRepo userDeactivationRepo;

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @Mock
    SimpMessagingTemplate messagingTemplate;

    private final User user = User.builder()
        .id(1L)
        .name("Taras")
        .email("test@gmail.com")
        .role(ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();

    private final User user1 = User.builder()
        .uuid("444e66e8-8daa-4cb0-8269-a8d856e7dd15")
        .name("Nazar")
        .build();

    private final UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();
    private final User user2 = User.builder()
        .id(2L)
        .name("Test Testing")
        .email("test2@gmail.com")
        .role(Role.ROLE_MODERATOR)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .build();
    private final UserVO userVO2 =
        UserVO.builder()
            .id(2L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(Role.ROLE_MODERATOR)
            .userStatus(ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private final UbsCustomerDto ubsCustomerDto =
        UbsCustomerDto.builder()
            .name("Nazar")
            .phoneNumber("09876543322")
            .email("nazar98struk.gmail.com")
            .build();

    private final Long userId = user.getId();

    private final Long habitId = 1L;
    private final Long userId2 = user2.getId();
    private final String userEmail = user.getEmail();

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllByEmailNotification() {
        when(userRepo.findAllByEmailNotification(any(EmailNotification.class)))
            .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(Collections.singletonList(userVO),
            userService.findAllByEmailNotification(EmailNotification.IMMEDIATELY));
    }

    @Test
    void scheduleDeleteDeactivatedUsers() {
        when(userRepo.scheduleDeleteDeactivatedUsers()).thenReturn(1);
        assertEquals(1, userService.scheduleDeleteDeactivatedUsers());
    }

    @Test
    void findAllUsersCitiesTest() {
        Long userId = 1L;
        UserCityDto userCityDto = mock(UserCityDto.class);

        when(greenCityRemoteClient.findAllUsersCities(userId))
            .thenReturn(userCityDto);

        UserCityDto actualResult = userService.findAllUsersCities(userId);

        assertEquals(userCityDto, actualResult);
    }

    @Test
    void findAllUsersCitiesExceptionTest() {
        Long userId = 1L;

        when(greenCityRemoteClient.findAllUsersCities(userId))
            .thenThrow(new RuntimeException());

        assertThrows(
            RuntimeException.class,
            () -> userService.findAllUsersCities(userId));

        verify(greenCityRemoteClient).findAllUsersCities(userId);
    }

    @Test
    void findAllRegistrationMonthsMap() {
        Map<Integer, Long> expected = Collections.singletonMap(1, 1L);
        when(userRepo.findAllRegistrationMonthsMap()).thenReturn(expected);
        assertEquals(expected, userService.findAllRegistrationMonthsMap());
    }

    @Test
    void saveTest() {
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(userService.findByEmail(userEmail)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(userVO, userService.save(userVO));
    }

    @Test
    void updateEmployeeEmailTest() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        String email = "test1@gmail.com";
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(user));
        when(userRepo.existsUserByEmail(email)).thenReturn(false);
        userService.updateEmployeeEmail(email, uuid);
        assertEquals(email, user.getEmail());
        verify(userRepo).findUserByUuid(uuid);
        verify(userRepo).existsUserByEmail(email);
    }

    @Test
    void updateEmployeeWithSameEmailTest() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        String email = "test@gmail.com";
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(user));
        userService.updateEmployeeEmail(email, uuid);
        assertEquals(email, user.getEmail());
        verify(userRepo).findUserByUuid(uuid);
    }

    @Test
    void updateEmployeeEmailThrowsUsernameNotFoundExceptionTest() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> userService.updateEmployeeEmail("test@mail.com", uuid));
        verify(userRepo).findUserByUuid(uuid);
    }

    @Test
    void updateEmployeeEmailThrowsBadRequestExceptionTest() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        String email = "test1@gmail.com";
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(user));
        when(userRepo.existsUserByEmail(email)).thenReturn(true);
        assertThrows(BadRequestException.class,
            () -> userService.updateEmployeeEmail(email, uuid));
        verify(userRepo).findUserByUuid(uuid);
        verify(userRepo).existsUserByEmail(email);
    }

    @Test
    void updateUserStatusDeactivatedTest() {
        when(userRepo.findById(userId2)).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(Optional.of(user2), UserVO.class)).thenReturn(userVO2);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(userRepo.save(any())).thenReturn(user);

        UserStatusDto value = new UserStatusDto();
        value.setUserStatus(DEACTIVATED);
        when(modelMapper.map(user, UserStatusDto.class)).thenReturn(value);
        assertEquals(DEACTIVATED, userService.updateStatus(userId, DEACTIVATED, any()).getUserStatus());
    }

    @Test
    void updateUserStatusLowRoleLevelException() {
        user.setRole(Role.ROLE_MODERATOR);
        userVO.setRole(Role.ROLE_MODERATOR);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertThrows(LowRoleLevelException.class, () -> userService.updateStatus(userId, DEACTIVATED, "email"));
    }

    @Test
    void updateRoleTest() {
        // given
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setRole(Role.ROLE_MODERATOR);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user, UserRoleDto.class)).thenReturn(userRoleDto);
        user.setRole(Role.ROLE_MODERATOR);

        // then
        assertEquals(
            Role.ROLE_MODERATOR,
            userService.updateRole(userId, Role.ROLE_MODERATOR, user2.getEmail()).getRole());
    }

    @Test
    void updateRoleOnTheSameUserTest() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(BadUpdateRequestException.class, () -> userService.updateRole(userId, null, userEmail));
    }

    @Test
    void updateRoleOfNonExistingUser() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateRole(userId, null, userEmail));
    }

    @Test
    void findByIdTest() {
        Long id = 1L;

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(userVO, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findIdByEmail() {
        String email = "email";
        when(userRepo.findIdByEmail(email)).thenReturn(Optional.of(2L));
        assertEquals(2L, (long) userService.findIdByEmail(email));
    }

    @Test
    void findIdByEmailNotFound() {
        String email = "email";

        assertThrows(WrongEmailException.class, () -> userService.findIdByEmail(email));
    }

    @Test
    void findUuIdByEmailTest() {
        String email = "email";
        when(userRepo.findUuidByEmail(email)).thenReturn(Optional.of("email"));
        assertEquals("email", userService.findUuIdByEmail(email));
    }

    @Test
    void findUuIdByEmailNotFoundTest() {
        String email = "email";

        assertThrows(WrongEmailException.class, () -> userService.findUuIdByEmail(email));
    }

    @Test
    void findAllTest() {
        List<UserVO> userVOList = List.of(ModelUtils.getUserVO(), ModelUtils.getUserVO(), ModelUtils.getUserVO());
        when(modelMapper.map(userRepo.findAll(), new TypeToken<List<UserVO>>() {
        }.getType())).thenReturn(userVOList);
        assertEquals(userVOList, userService.findAll());

    }

    @Test
    void findByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User myUser = new User();
        myUser.setName("Roman Romanovich");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Romanovich");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(myUser), pageable, 1);
        List<UserForListDto> userForListDtos = Collections.singletonList(userForListDto);

        PageableDto<UserForListDto> userPageableDto =
            new PageableDto<>(userForListDtos,
                userForListDtos.size(), 0, 1);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(pageable)).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.findByPage(pageable));
        verify(userRepo, times(1)).findAll(pageable);
    }

    @Test
    void getRoles() {
        user.setRole(ROLE_USER);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RoleDto result = userService.getRoles(1L);

        assertEquals(ROLE_USER, result.getRoles()[0]);
        assertThrows(NotFoundException.class, () -> userService.getRoles(0L));
    }

    @Test
    void getEmailNotificationsStatusesTest() {
        String email = "test@gmail.com";
        user.setEmailNotification(EmailNotification.IMMEDIATELY);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        EmailNotification result = userService.getEmailNotificationsStatuses(email);

        assertEquals(EmailNotification.IMMEDIATELY, result);
    }

    @Test
    void getUsersByFilter() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User myUser = new User();
        myUser.setName("Roman Bezos");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Bezos");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(myUser), pageable, 1);
        List<UserForListDto> userForListDtos = Collections.singletonList(userForListDto);

        PageableDto<UserForListDto> userPageableDto =
            new PageableDto<>(userForListDtos,
                userForListDtos.size(), 0, 1);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(usersPage);
        FilterUserDto filterUserDto = new FilterUserDto();
        assertEquals(userPageableDto, userService.getUsersByFilter(filterUserDto, pageable));
    }

    @Test
    void getUserUpdateDtoByEmail() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        when(modelMapper.map(any(), any())).thenReturn(userUpdateDto);
        UserUpdateDto userInitialsByEmail = userService.getUserUpdateDtoByEmail("");
        assertEquals(userInitialsByEmail.getName(), user.getName());
        assertEquals(userInitialsByEmail.getEmailNotification(), user.getEmailNotification());
    }

    @Test
    void update() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        assertEquals(userUpdateDto, userService.update(userUpdateDto, ""));
        verify(userRepo, times(1)).save(any());
    }

    @Test
    void updateUserRefreshTokenForUserWithExistentIdTest() {
        when(userRepo.updateUserRefreshToken("foo", userId)).thenReturn(1);
        int updatedRows = userService.updateUserRefreshToken("foo", userId);
        assertEquals(1, updatedRows);
    }

    @Test
    void getActivatedUsersAmountTest() {
        when(userRepo.countAllByUserStatus(ACTIVATED)).thenReturn(1L);
        long activatedUsersAmount = userService.getActivatedUsersAmount();
        assertEquals(1L, activatedUsersAmount);
    }

    @Test
    void updateUserProfilePictureNotUpdatedExceptionTest() {
        UserProfilePictureDto userProfilePictureDto = ModelUtils.getUserProfilePictureDto();
        userProfilePictureDto.setProfilePicturePath(null);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class,
            () -> userService.updateUserProfilePicture(null, "testmail@gmail.com",
                "test"));
    }

    @Test
    void getUserProfileStatistics() {
        when(restClient.findAmountOfPublishedNews(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(restClient.findAmountOfAcquiredHabits(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(restClient.findAmountOfHabitsInProgress(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(restClient.findAmountOfEventsAttendedByUser(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(restClient.findAmountOfEventsOrganizedByUser(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);

        assertEquals(ModelUtils.USER_PROFILE_STATISTICS_DTO,
            userService.getUserProfileStatistics(TestConst.SIMPLE_LONG_NUMBER));
        assertNotEquals(ModelUtils.USER_PROFILE_STATISTICS_DTO,
            userService.getUserProfileStatistics(TestConst.SIMPLE_LONG_NUMBER_BAD_VALUE));

        verify(restClient, times(2)).findAmountOfPublishedNews(anyLong());
        verify(restClient, times(2)).findAmountOfAcquiredHabits(anyLong());
        verify(restClient, times(2)).findAmountOfHabitsInProgress(anyLong());
        verify(restClient, times(2)).findAmountOfEventsAttendedByUser(anyLong());
        verify(restClient, times(2)).findAmountOfEventsOrganizedByUser(anyLong());
    }

    @Test
    void searchBy() {
        Pageable pageable = PageRequest.of(1, 3);
        user.setUserCredo("credo");
        Page<User> userPages = new PageImpl<>(List.of(user, user, user), pageable, 3);
        when(userRepo.searchBy(pageable, "query"))
            .thenReturn(userPages);
        when(modelMapper.map(user, UserManagementDto.class)).thenReturn(ModelUtils.CREATE_USER_MANAGER_DTO);
        List<UserManagementDto> users = userPages.stream()
            .map(myUser -> modelMapper.map(myUser, UserManagementDto.class))
            .collect(Collectors.toList());
        PageableAdvancedDto<UserManagementDto> pageableAdvancedDto = new PageableAdvancedDto<>(
            users,
            userPages.getTotalElements(),
            userPages.getPageable().getPageNumber(),
            userPages.getTotalPages(),
            userPages.getNumber(),
            userPages.hasPrevious(),
            userPages.hasNext(),
            userPages.isFirst(),
            userPages.isLast());
        assertEquals(pageableAdvancedDto, userService.searchBy(pageable, "query"));
    }

    @Test
    void findUserByName() {
        Pageable pageable = PageRequest.of(1, 3);
        user.setUserCredo("credo");
        Page<User> pages = new PageImpl<>(List.of(user, user, user), pageable, 3);
        when(userRepo.findAllUsersByName("martin", pageable, 1L))
            .thenReturn(pages);
        when(modelMapper.map(pages.getContent(), new TypeToken<List<UserAllFriendsDto>>() {
        }.getType()))
            .thenReturn(CREATE_USER_ALL_FRIENDS_DTO);
        PageableDto<UserAllFriendsDto> pageableDto = new PageableDto<>(
            CREATE_USER_ALL_FRIENDS_DTO,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
        assertEquals(pageableDto, userService.findUserByName("martin", pageable, 1L));
    }

    @Test
    void saveUserProfileTest() {
        var request = ModelUtils.getUserProfileDtoRequest();
        var myUser = ModelUtils.getUserWithSocialNetworks();
        Set<UserNotificationPreference> preferences = new HashSet<>();
        preferences.add(UserNotificationPreference.builder()
            .emailPreference(EmailPreference.SYSTEM)
            .periodicity(EmailPreferencePeriodicity.DAILY)
            .build());
        preferences.add(UserNotificationPreference.builder()
            .emailPreference(EmailPreference.LIKES)
            .periodicity(EmailPreferencePeriodicity.TWICE_A_DAY)
            .build());
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();

        myUser.setNotificationPreferences(preferences);
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId))
            .thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, "test@gmail.com");

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);
        verify(userRepo).findByEmail("test@gmail.com");
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullValuesTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(null).build());
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();

        var myUser = ModelUtils.getUserWithSocialNetworks();
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String result = userService.saveUserProfile(request, "test@gmail.com");
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail("test@gmail.com");
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullLatitudeTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(1.0d).build());
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();

        var myUser = ModelUtils.getUserWithSocialNetworks();
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String result = userService.saveUserProfile(request, "test@gmail.com");
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail("test@gmail.com");
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullLongitudeTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(1.0d).longitude(null).build());
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();

        var myUser = ModelUtils.getUserWithSocialNetworks();
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String result = userService.saveUserProfile(request, "test@gmail.com");
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail("test@gmail.com");
        verify(userRepo).save(myUser);
    }

    @Test
    void updateUserProfileLocationWithTwoAssignedUsersTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        request.setName("Dmutro");
        var myUser = ModelUtils.getUserWithSocialNetworks();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);
        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
        verify(userRepo).save(myUser);
    }

    @Test
    void updateUserProfileLocationWhenUserHasAUserLocationTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);

        verify(userRepo).findByEmail(email);
        verify(userRepo).save(myUser);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
    }

    @Test
    void testUpdateUserProfileDeleteLocation() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(null, null);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        // myUser.getUserLocation().getUsers().add(myUser);
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);
        verify(userRepo).save(myUser);
        // assertNull(myUser.getUserLocation());
    }

    @Test
    void updateUserProfileRemoveUserFromUserLocationListTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);

        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
        verify(userRepo).save(myUser);
    }

    @Test
    void updateUserProfileLocationWhenUserModifyUserLocationTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);

        verify(userRepo).findByEmail(email);
        verify(userRepo).save(myUser);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
    }

    @Test
    void saveUserProfileThrowWrongEmailExceptionTest() {
        var request = UserProfileDtoRequest.builder().build();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        Exception thrown = assertThrows(WrongEmailException.class,
            () -> userService.saveUserProfile(request, "test@gmail.com"));
        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "test@gmail.com", thrown.getMessage());
        verify(userRepo).findByEmail(anyString());
    }

    @Test
    void saveUserProfileWhenLocationIsNotUpdatedTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        String email = "test@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);

        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
        verify(userRepo).save(myUser);
    }

    @Test
    void getUserProfileInformationTest() {
        UserProfileDtoResponse response = new UserProfileDtoResponse();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDtoResponse.class)).thenReturn(response);
        assertEquals(response, userService.getUserProfileInformation(1L));
        verify(userRepo).findById(1L);
    }

    @Test
    void getUserProfileInformationWithUserLocationTest() {
        Long userId = 1L;
        UserProfileDtoResponse response = new UserProfileDtoResponse();
        UserLocationDto userLocationDto = new UserLocationDto();
        response.setUserLocationDto(userLocationDto);

        when(greenCityRemoteClient.findUserLocationByUserId(userId))
            .thenReturn(userLocationDto);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDtoResponse.class)).thenReturn(response);
        assertEquals(response, userService.getUserProfileInformation(userId));
        verify(userRepo).findById(userId);
    }

    @Test
    void getUserProfileInformationExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.getUserProfileInformation(null));
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.checkIfTheUserIsOnline(null));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        User myUser = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(myUser));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));

        assertTrue(userService.checkIfTheUserIsOnline(1L));
        verify(userRepo).findById(anyLong());
        verify(userRepo).findLastActivityTimeById(anyLong());
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.checkIfTheUserIsOnline(1L));
        verify(userRepo).findById(anyLong());
        verify(userRepo).findLastActivityTimeById(anyLong());
    }

    @Test
    void findUserForManagementByPage() {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<User> userList = Collections.singletonList(ModelUtils.getUser());
        Page<User> users = new PageImpl<>(userList, pageable, userList.size());
        List<UserManagementDto> userManagementDtos =
            users.getContent().stream()
                .map(myUser -> modelMapper.map(myUser, UserManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<UserManagementDto> userManagementDtoPageableDto = new PageableAdvancedDto<>(
            userManagementDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages(),
            users.getNumber(),
            users.hasPrevious(),
            users.hasNext(),
            users.isFirst(),
            users.isLast());
        when(userRepo.findAll(pageable)).thenReturn(users);
        assertEquals(userManagementDtoPageableDto, userService.findUserForManagementByPage(pageable));
        verify(userRepo).findAll(pageable);
    }

    @Test
    void updateUser() {
        UserManagementUpdateDto userManagementUpdateDto = ModelUtils.getUserManagementUpdateDto();
        User excepted = user;
        excepted.setName(userManagementUpdateDto.getName());
        excepted.setEmail(userManagementUpdateDto.getEmail());
        excepted.setRole(userManagementUpdateDto.getRole());
        excepted.setUserCredo(userManagementUpdateDto.getUserCredo());
        excepted.setUserStatus(userManagementUpdateDto.getUserStatus());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.updateUser(1L, userManagementUpdateDto);
        assertEquals(excepted, user);
    }

    @Test
    void findNotDeactivatedByEmail() {
        String email = "test@gmail.com";
        user.setEmail(email);
        when(userRepo.findNotDeactivatedByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(Optional.of(userVO), userService.findNotDeactivatedByEmail(email));
    }

    @Test
    void findNotDeactivatedByEmailShouldThrowNotFoundException() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        Exception thrown = assertThrows(NotFoundException.class,
            () -> userService.findNotDeactivatedByEmail("test@gmail.com"));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, thrown.getMessage());
    }

    @Test
    void getUserAndSixFriendsWithOnlineStatus() {
        List<UserWithOnlineStatusDto> sixFriendsWithOnlineStatusDtos;
        sixFriendsWithOnlineStatusDtos = Collections.singletonList(user)
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(greenCityRemoteClient.getSixFriendsIdsWithTheHighestRating(userId))
            .thenReturn(Collections.singletonList(user.getId()));
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(true)
            .build();
        UserAndFriendsWithOnlineStatusDto userAndFriendsWithOnlineStatusDto =
            UserAndFriendsWithOnlineStatusDto.builder()
                .user(userWithOnlineStatusDto)
                .friends(sixFriendsWithOnlineStatusDtos)
                .build();
        assertEquals(userAndFriendsWithOnlineStatusDto, userService.getUserAndSixFriendsWithOnlineStatus(userId));
    }

    @Test
    void getAllFriendsWithTheOnlineStatus() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Page<Long> usersIdsPage = new PageImpl<>(
            usersPage.getContent().stream()
                .map(User::getId)
                .toList(),
            pageable,
            1);
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(true)
            .build();
        List<UserWithOnlineStatusDto> friendsWithOnlineStatusDtos;
        friendsWithOnlineStatusDtos = usersPage
            .getContent()
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());
        new UserAndAllFriendsWithOnlineStatusDto();
        UserAndAllFriendsWithOnlineStatusDto userAndAllFriendsWithOnlineStatusDto =
            UserAndAllFriendsWithOnlineStatusDto.builder()
                .user(userWithOnlineStatusDto)
                .friends(new PageableDto<>(friendsWithOnlineStatusDtos, usersPage.getTotalElements(),
                    usersPage.getPageable().getPageNumber(), usersPage.getTotalPages()))
                .build();

        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(greenCityRemoteClient.getAllUserFriendsIds(userId, pageable)).thenReturn(usersIdsPage);

        assertEquals(userAndAllFriendsWithOnlineStatusDto,
            userService.getAllFriendsWithTheOnlineStatus(userId, pageable));
    }

    @Test
    void deactivateUser() {
        String uuid = "user-uuid";
        String reason = "Account closed by user request";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto(reason);
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(Role.ROLE_ADMIN)
            .build();

        User foundUser = User.builder()
            .id(2L)
            .role(ROLE_USER)
            .languageId(languageId)
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(foundUser));
        when(userDeactivationRepo.save(any())).thenReturn(null);
        when(userRepo.save(foundUser)).thenReturn(foundUser);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        UserDeactivationReasonDto result = userService.deactivateUser(uuid, request, userVO);

        assertNotNull(result);
        assertEquals(foundUser.getEmail(), result.getEmail());
        assertEquals(foundUser.getName(), result.getName());
        assertEquals(reason, result.getDeactivationReason());
        assertEquals(languageVO.getCode(), result.getLang());
    }

    @Test
    void getDeactivationReason() {
        List<String> deactivationReaons = List.of();
        User myUser = ModelUtils.getUser();
        user.setLanguageId(1L);
        UserDeactivationReason test = UserDeactivationReason.builder()
            .id(1L)
            .user(myUser)
            .reason("test")
            .dateTimeOfDeactivation(LocalDateTime.now())
            .build();
        when(userDeactivationRepo.getLastDeactivationReasons(1L)).thenReturn(Optional.of(test));
        assertEquals(deactivationReaons, userService.getDeactivationReason(1L, "en"));
        assertEquals(deactivationReaons, userService.getDeactivationReason(1L, "ua"));
    }

    @Test
    void deactivateAllUsers() {
        List<Long> longList = List.of(1L, 2L);
        assertEquals(longList, userService.deactivateAllUsers(longList));
    }

    @Test
    void setActivatedStatus() {
        User user = ModelUtils.getUser();
        Long languageId = ModelUtils.getLanguageId();
        user.setLanguageId(languageId);
        user.setUserStatus(ACTIVATED);
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        UserActivationDto expectedResult = UserActivationDto.builder()
            .email(user.getEmail())
            .name(user.getName())
            .lang(languageVO.getCode())
            .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);

        UserActivationDto actualResult = userService.setActivatedStatus(userId);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateUserLanguage() {
        User user = ModelUtils.getUser();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        user.setLanguageId(languageId);

        when(greenCityRemoteClient.languageExistsById(languageId)).thenReturn(true);
        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        userService.updateUserLanguage(1L, languageId);

        verify(userRepo).save(user);
    }

    @Test
    void updateUserLanguageNotFoundExeption() {
        User user = ModelUtils.getUser();
        Long languageId = ModelUtils.getLanguageId();
        user.setLanguageId(languageId);

        when(greenCityRemoteClient.languageExistsById(languageId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.updateUserLanguage(1L, languageId));
    }

    @Test
    void updateUserLanguageUserNotFoundExeption() {
        User user = ModelUtils.getUser();
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        user.setLanguageId(languageId);

        when(greenCityRemoteClient.findLanguageById(languageId)).thenReturn(languageVO);
        when(userRepo.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.updateUserLanguage(1L, 1L));
    }

    @Test
    void getAvailableCustomToDoListItem() {
        CustomToDoListItemResponseDto customToDoListItemResponseDto =
            new CustomToDoListItemResponseDto(1L, "test");
        when(restClient.getAllAvailableCustomToDoListItems(userId, habitId))
            .thenReturn(Collections.singletonList(customToDoListItemResponseDto));

        assertEquals(Collections.singletonList(customToDoListItemResponseDto),
            userService.getAvailableCustomToDoListItems(userId, habitId));
    }

    @Test
    void searchTest() {
        Pageable pageable = PageRequest.of(0, 20);
        UserManagementViewDto userViewDto =
            UserManagementViewDto.builder()
                .id("1L")
                .name("vivo")
                .email("test@ukr.net")
                .userCredo("Hello")
                .role("1")
                .userStatus("1")
                .build();
        UserManagementVO userManagementVO =
            UserManagementVO.builder()
                .id(1L)
                .name("vivo")
                .email("test@ukr.net")
                .userCredo("Hello")
                .role(ROLE_USER)
                .userStatus(ACTIVATED)
                .build();
        List<UserManagementVO> userManagementVOS = Collections.singletonList(userManagementVO);
        List<User> users = Collections.singletonList(new User());
        Page<User> pageUsers = new PageImpl<>(users, pageable, 0);
        when(userRepo.findAll(any(UserSpecification.class), eq(pageable))).thenReturn(pageUsers);
        when(modelMapper.map(users.getFirst(), UserManagementVO.class)).thenReturn(userManagementVO);
        PageableAdvancedDto<UserManagementVO> actual = new PageableAdvancedDto<>(userManagementVOS, 1, 0, 1, 0,
            false, false, true, true);
        PageableAdvancedDto<UserManagementVO> expected = userService.search(pageable, userViewDto);
        assertEquals(expected, actual);
    }

    @Test
    void findUbsCustomerDtoByUuidTest() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(user1));
        when(modelMapper.map(Optional.of(user1), UbsCustomerDto.class)).thenReturn(ubsCustomerDto);
        when(userService.findUbsCustomerDtoByUuid(uuid)).thenReturn(ubsCustomerDto);
        assertEquals(ubsCustomerDto, userService.findUbsCustomerDtoByUuid(uuid));
    }

    @Test
    void markUserDeactivated() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        User myUser = ModelUtils.getUser();
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(myUser));
        myUser.setUserStatus(DEACTIVATED);
        when(userRepo.save(myUser)).thenReturn(myUser);
        userService.markUserAsDeactivated(uuid);
        verify(userRepo).save(myUser);

    }

    @Test
    void markUserDeactivatedException() {
        String uuid = "uuid";
        assertThrows(NotFoundException.class,
            () -> userService.markUserAsDeactivated(uuid));
    }

    @Test
    void markUserActivated() {
        String uuid = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        User myUser = ModelUtils.getUser();
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(myUser));
        myUser.setUserStatus(ACTIVATED);
        when(userRepo.save(myUser)).thenReturn(myUser);
        userService.markUserAsActivated(uuid);
        verify(userRepo).save(myUser);

    }

    @Test
    void markUserActivatedException() {
        String uuid = "uuid";
        assertThrows(NotFoundException.class,
            () -> userService.markUserAsActivated(uuid));
    }

    @Test
    void findUserForAchievementTest() {
        Long id = 1L;
        UserVOAchievement userVOAchievement = UserVOAchievement.builder().id(id).build();
        User myUser = User.builder().id(id).build();
        when(userRepo.findUserForAchievement(id)).thenReturn(Optional.of(myUser));
        when(modelMapper.map(myUser, UserVOAchievement.class)).thenReturn(userVOAchievement);
        assertEquals(userVOAchievement, userService.findUserForAchievement(id));
        verify(userRepo, times(1)).findUserForAchievement(id);
    }

    @Test
    void createUbsRecordTest() {
        Long id = 1L;
        User myUser = new User();
        myUser.setId(1L);
        when(userRepo.findById(id)).thenReturn(Optional.of(myUser));
        when(modelMapper.map(myUser, UserVO.class)).thenReturn(userVO);
        UbsTableCreationDto actual = UbsTableCreationDto.builder().uuid(myUser.getUuid()).build();
        assertEquals(actual, userService.createUbsRecord(userVO));
    }

    @Test
    void createUbsRecordThrowNotFoundExceptionTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        Exception thrown = assertThrows(NotFoundException.class,
            () -> userService.createUbsRecord(userVO));
        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + 1L, thrown.getMessage());
        verify(userRepo).findById(1L);
    }

    @Test
    void deleteUserProfilePictureTest() {
        String email = "test@gmail.com";
        String picture = "picture";
        User myUser = new User();
        myUser.setEmail(email);
        myUser.setProfilePicturePath(picture);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(modelMapper.map(myUser, UserVO.class)).thenReturn(userVO);
        userService.deleteUserProfilePicture(email);
        assertNull(myUser.getProfilePicturePath());
    }

    @Test
    void findAdminByIdTest() {
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_ADMIN));
        when(modelMapper.map(TEST_ADMIN, UserVO.class)).thenReturn(TEST_USER_VO);

        UserVO actual = userService.findAdminById(2L);

        assertEquals(TEST_USER_VO, actual);
    }

    @Test
    void findAdminByIdThrowsExceptionTest() {
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_USER));

        assertThrows(LowRoleLevelException.class,
            () -> userService.findAdminById(2L));
    }

    @ParameterizedTest
    @MethodSource("provideUuidOptionalUserResultForCheckIfUserExistsByUuidTest")
    void checkIfUserExistsByUuidTest(String uuid, Optional<User> user, boolean existence) {
        when(userRepo.findUserByUuid(uuid)).thenReturn(user);
        assertEquals(existence, userService.checkIfUserExistsByUuid(uuid));
    }

    private static Stream<Arguments> provideUuidOptionalUserResultForCheckIfUserExistsByUuidTest() {
        return Stream.of(
            Arguments.of("444e66e8-8daa-4cb0-8269-a8d856e7dd15", Optional.of(ModelUtils.getUser()), true),
            Arguments.of("uuid", Optional.empty(), false));
    }

    @Test
    void editUserRatingTest() {
        UserAddRatingDto userRatingDto = UserAddRatingDto.builder()
            .id(1L)
            .rating(200D)
            .build();

        userService.updateUserRating(userRatingDto);
        verify(greenCityRemoteClient).updateUserRating(userRatingDto);
    }

    @Test
    void updateStatusWithFailedCheckUpdatableUserTest() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        Long id = user2.getId();
        assertThrows(BadUpdateRequestException.class,
            () -> userService.updateStatus(id, DEACTIVATED, "email"));
        verify(userRepo).findByEmail(any());
        verify(modelMapper).map(user2, UserVO.class);
    }

    @Test
    void updateUserProfilePictureTest() {
        String fileName = "test.txt";
        String content = "test file content";
        byte[] bytes = content.getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, "text/plain", bytes);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(restClient.uploadImage(any())).thenReturn("picturePath");
        when(modelMapper.map(any(), any())).thenReturn(userVO);
        UserVO actual = userService.updateUserProfilePicture(file, "testmail@gmail.com", null);
        assertEquals(userVO, actual);
        verify(restClient).uploadImage(any());
        verify(modelMapper).map(any(), any());
        verify(userRepo).findByEmail(anyString());
    }

    @Test
    void updateUserProfilePictureBaseTest() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), any())).thenReturn(null);
        assertThrows(BadRequestException.class,
            () -> userService.updateUserProfilePicture(null, "testmail@gmail.com", "test"));
        verify(modelMapper).map(any(), any());
        verify(userRepo).findByEmail(anyString());
    }

    @Test
    void getDeactivationReasonUkTest() {
        List<String> test1 = List.of();
        User myUser = ModelUtils.getUser();
        myUser.setLanguageId(1L);
        UserDeactivationReason test = UserDeactivationReason.builder()
            .id(1L)
            .user(myUser)
            .reason("test")
            .dateTimeOfDeactivation(LocalDateTime.now())
            .build();
        when(userDeactivationRepo.getLastDeactivationReasons(1L)).thenReturn(Optional.of(test));
        assertEquals(test1, userService.getDeactivationReason(1L, "uk"));
    }

    @Test
    void updateUserLastActivityTimeByEmailTest() {
        LocalDateTime currentTime = LocalDateTime.now();
        userService.updateUserLastActivityTimeByEmail(userEmail, currentTime);
        verify(userRepo).updateUserLastActivityTimeByEmail(userEmail, currentTime);
    }

    @Test
    void getUsersOnlineStatusTest() {
        var lastActivityTime = LocalDateTime.now().minusMinutes(1);
        var lastActivityTimestamp = Timestamp.valueOf(lastActivityTime);

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(getUser()));
        when(userRepo.getAllUsersByUsersId(List.of(1L))).thenReturn(List.of(User.builder().id(1L).build()));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(lastActivityTimestamp));

        userService.checkUsersOnlineStatus(new UsersOnlineStatusRequestDto(1L, List.of(1L)));
        verify(messagingTemplate).convertAndSend(eq("/topic/1/usersOnlineStatus"), anyList());
        verify(userRepo).findById(1L);
        verify(userRepo).getAllUsersByUsersId(List.of(1L));
        verify(userRepo).findLastActivityTimeById(anyLong());
    }

    @Test
    void testDeactivateUserUserNotFoundById() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserUserNotFoundByUuid() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(new User()));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserCannotDeactivateYourself() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(ROLE_ADMIN)
            .languageId(ModelUtils.getLanguageId())
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(requestedUser));

        assertThrows(UserDeactivationException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserCannotDeactivateOthers() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(ROLE_USER)
            .build();

        User foundUser = User.builder()
            .id(2L)
            .role(ROLE_USER)
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(foundUser));

        assertThrows(UserDeactivationException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserAdminCannotDeactivateOtherAdmin() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(ROLE_ADMIN)
            .build();

        User foundUser = User.builder()
            .id(2L)
            .role(ROLE_ADMIN)
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(foundUser));

        assertThrows(UserDeactivationException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserNoPermissionsToDeactivateUser() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(ROLE_MODERATOR)
            .build();

        User foundUser = User.builder()
            .id(2L)
            .role(ROLE_ADMIN)
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(foundUser));

        assertThrows(UserDeactivationException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void testDeactivateUserUnauthorizedRoleToDeactivateUser() {
        String uuid = "user-uuid";
        DeactivateUserRequestDto request = new DeactivateUserRequestDto("Reason");

        User requestedUser = User.builder()
            .id(userVO.getId())
            .role(ROLE_USER)
            .build();

        User foundUser = User.builder()
            .id(2L)
            .role(ROLE_ADMIN)
            .build();

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(requestedUser));
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(foundUser));

        assertThrows(UserDeactivationException.class, () -> userService.deactivateUser(uuid, request, userVO));
    }

    @Test
    void findAllActivatedUserIdsOkTest() {
        List<Long> input = List.of(1L, 2L, 3L, 4L, 5L);
        when(userRepo.findAllActivatedUserIdsFromList(input)).thenReturn(List.of(1L, 2L, 3L));

        List<Long> result = userService.findAllActivatedUserIds(input);

        assertEquals(3, result.size());
        verify(userRepo, times(1)).findAllActivatedUserIdsFromList(input);
    }

    @Test
    void findAllActivatedUserIdsNoResultTest() {
        List<Long> input = List.of(1L, 2L, 3L);
        when(userRepo.findAllActivatedUserIdsFromList(input)).thenReturn(List.of());

        List<Long> result = userService.findAllActivatedUserIds(input);

        assertEquals(0, result.size());
        verify(userRepo, times(1)).findAllActivatedUserIdsFromList(input);
    }

    @Test
    void findAllActivatedUserIdsNullInputTest() {
        when(userRepo.findAllActivatedUserIds()).thenReturn(List.of(1L, 2L, 3L));

        List<Long> result = userService.findAllActivatedUserIds(null);

        assertEquals(3, result.size());
        verify(userRepo, times(1)).findAllActivatedUserIds();
    }

    @Test
    void findAllActivatedUserIdsNullInputNoResultTest() {
        when(userRepo.findAllActivatedUserIds()).thenReturn(List.of());

        List<Long> result = userService.findAllActivatedUserIds(null);

        assertEquals(0, result.size());
        verify(userRepo, times(1)).findAllActivatedUserIds();
    }
}
