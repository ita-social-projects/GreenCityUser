package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.GreenCityRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.constant.UpdateConstants;
import greencity.dto.CoordinatesDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.todolist.CustomToDoListItemResponseDto;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UpdateUserNameDto;
import greencity.dto.user.UpdateUserPicturePathDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserAllFriendsDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserEmailDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.dto.user.UserVOShort;
import greencity.dto.user.UserWithOnlineStatusDto;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import static greencity.enums.Role.ROLE_USER;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.ProjectName;
import greencity.enums.RetryableTaskType;
import greencity.enums.Role;
import greencity.enums.ServiceUserStatus;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.exception.exceptions.Base64DecodedException;
import greencity.exception.exceptions.GreenCityServiceException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.filters.UserSpecification;
import greencity.repository.LanguageRepo;
import greencity.repository.UserRepo;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static greencity.ModelUtils.CREATE_USER_ALL_FRIENDS_DTO;
import static greencity.ModelUtils.TEST_ADMIN;
import static greencity.ModelUtils.TEST_USER;
import static greencity.ModelUtils.TEST_USER_VO;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVOAdvancedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    SocialNetworkImageService socialNetworkImageService;

    @Mock
    SocialNetworkService socialNetworkService;

    @Mock
    LanguageRepo languageRepo;

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    private UserAddRatingDto userRatingDto;
    @Mock
    private RetryableTaskService retryableTaskService;
    @Mock
    private FileService fileService;

    private final User user = User.builder()
        .id(1L)
        .name("Taras")
        .email(TestConst.EMAIL)
        .role(ROLE_USER)
        .userStatus(UserStatus.VERIFIED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .language(new Language(1L, "uk", "Ukrainian", List.of()))
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
        .userStatus(UserStatus.VERIFIED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .build();
    private final User user2 = User.builder()
        .id(2L)
        .name("Test Testing")
        .email("test2@gmail.com")
        .role(Role.ROLE_MODERATOR)
        .userStatus(UserStatus.VERIFIED)
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
            .userStatus(UserStatus.VERIFIED)
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

    private final String userEmail = user.getEmail();

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllByEmailNotification() {
        UserVOShort userVOShort = ModelUtils.getUserVOShortDto();
        when(userRepo.findAllByEmailNotification(any(EmailNotification.class)))
            .thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserVOShort.class)).thenReturn(userVOShort);
        assertEquals(Collections.singletonList(userVOShort),
            userService.findAllByEmailNotification(EmailNotification.IMMEDIATELY));
    }

    @Test
    void findAllUsersCitiesTest() {
        UserCityDto userCityDto = mock(UserCityDto.class);

        when(greenCityRemoteClient.findAllUsersCities(userId))
            .thenReturn(userCityDto);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        UserCityDto actualResult = userService.findAllUsersCities(userId);

        assertEquals(userCityDto, actualResult);
    }

    @Test
    void findAllUsersCitiesExceptionTest() {
        when(greenCityRemoteClient.findAllUsersCities(userId))
            .thenThrow(new RuntimeException());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

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
    void updateUserStatusLowRoleLevelException() {
        user.setRole(Role.ROLE_MODERATOR);
        userVO.setRole(Role.ROLE_MODERATOR);
        UserVOShort userVOShort = ModelUtils.getUserVOShortDto();
        userVOShort.setRole(Role.ROLE_MODERATOR);

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVOShort.class)).thenReturn(userVOShort);

        assertThrows(LowRoleLevelException.class, () -> userService
            .updateStatus(userId, UserStatus.VERIFIED, "email"));
    }

    @Test
    void findAllByEmailPreferenceAndEmailPeriodicityTest() {
        List<User> users = List.of(ModelUtils.getUser(), ModelUtils.getUser());
        List<UserVOShort> expectedResult = List.of(ModelUtils.getUserVOShortDto(), ModelUtils.getUserVOShortDto());
        EmailPreference emailPreference = EmailPreference.LIKES;
        EmailPreferencePeriodicity emailPreferencePeriodicity = EmailPreferencePeriodicity.DAILY;

        when(userRepo.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference.name(),
            emailPreferencePeriodicity.name()))
                .thenReturn(users);
        when(modelMapper.map(any(User.class), eq(UserVOShort.class)))
            .thenReturn(ModelUtils.getUserVOShortDto());

        List<UserVOShort> actualResult = userService.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference,
            emailPreferencePeriodicity);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateRoleTest() {
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setRole(Role.ROLE_MODERATOR);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user, UserRoleDto.class)).thenReturn(userRoleDto);
        user.setRole(Role.ROLE_MODERATOR);

        assertEquals(
            Role.ROLE_MODERATOR,
            userService.updateRole(userId, Role.ROLE_MODERATOR, user2.getEmail()).getRole());
    }

    @Test
    void updateRoleByEmailTest() {
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setRole(Role.ROLE_MODERATOR);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user, UserRoleDto.class)).thenReturn(userRoleDto);
        user.setRole(Role.ROLE_MODERATOR);

        assertEquals(
            Role.ROLE_MODERATOR,
            userService.updateRole(user.getEmail(), Role.ROLE_MODERATOR, user2.getEmail()).getRole());
    }

    @Test
    void updateRoleOnTheSameUserTest() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(BadUpdateRequestException.class, () -> userService.updateRole(userId, null, userEmail));
    }

    @Test
    void updateRoleByEmailOnTheSameUserTest() {
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(BadUpdateRequestException.class, () -> userService.updateRole(userEmail, null, userEmail));
    }

    @Test
    void updateRoleOfNonExistingUser() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateRole(userId, null, userEmail));
    }

    @Test
    void updateRoleByEmailOfNonExistingUser() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        String email1 = user.getEmail();
        String email2 = user2.getEmail();
        assertThrows(WrongEmailException.class, () -> userService.updateRole(email1, null, email2));
    }

    @Test
    void findByIdTest() {
        Long id = 1L;
        UserVOShort userVOShort = ModelUtils.getUserVOShortDto();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVOShort.class)).thenReturn(userVOShort);
        assertEquals(userVOShort, userService.findById(id));
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
        List<UserVOShort> userVOShortList =
            List.of(ModelUtils.getUserVOShortDto(), ModelUtils.getUserVOShortDto(), ModelUtils.getUserVOShortDto());
        when(modelMapper.map(userRepo.findAll(), new TypeToken<List<UserVOShort>>() {
        }.getType())).thenReturn(userVOShortList);
        assertEquals(userVOShortList, userService.findAll());

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
        when(greenCityRemoteClient.findAmountOfPublishedNews(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(greenCityRemoteClient.findAmountOfAcquiredHabits(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(greenCityRemoteClient.findAmountOfHabitsInProgress(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(greenCityRemoteClient.findAmountOfEventsAttendedByUser(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(greenCityRemoteClient.findAmountOfEventsOrganizedByUser(TestConst.SIMPLE_LONG_NUMBER))
            .thenReturn(TestConst.SIMPLE_LONG_NUMBER);
        when(userRepo.findById(TestConst.SIMPLE_LONG_NUMBER)).thenReturn(Optional.of(user));
        when(userRepo.findById(TestConst.SIMPLE_LONG_NUMBER_BAD_VALUE)).thenReturn(Optional.of(user2));

        assertEquals(ModelUtils.USER_PROFILE_STATISTICS_DTO,
            userService.getUserProfileStatistics(TestConst.SIMPLE_LONG_NUMBER));
        assertNotEquals(ModelUtils.USER_PROFILE_STATISTICS_DTO,
            userService.getUserProfileStatistics(TestConst.SIMPLE_LONG_NUMBER_BAD_VALUE));

        verify(greenCityRemoteClient, times(2)).findAmountOfPublishedNews(anyLong());
        verify(greenCityRemoteClient, times(2)).findAmountOfAcquiredHabits(anyLong());
        verify(greenCityRemoteClient, times(2)).findAmountOfHabitsInProgress(anyLong());
        verify(greenCityRemoteClient, times(2)).findAmountOfEventsAttendedByUser(anyLong());
        verify(greenCityRemoteClient, times(2)).findAmountOfEventsOrganizedByUser(anyLong());
    }

    @Test
    void findUserByName() {
        Pageable pageable = PageRequest.of(1, 3);
        Page<User> pages = new PageImpl<>(List.of(user, user, user), pageable, 3);
        when(userRepo.findAllUsersByName(user.getName(), pageable, 1L))
            .thenReturn(pages);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(pages.getContent(), new TypeToken<List<UserAllFriendsDto>>() {
        }.getType()))
            .thenReturn(CREATE_USER_ALL_FRIENDS_DTO);
        PageableDto<UserAllFriendsDto> pageableDto = new PageableDto<>(
            CREATE_USER_ALL_FRIENDS_DTO,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
        assertEquals(pageableDto, userService.findUserByName(user.getName(), pageable, 1L));
    }

    @Test
    void saveUserProfileTest() {
        var request = ModelUtils.getUserProfileDtoRequest();
        var myUser = ModelUtils.getUserWithSocialNetworks();
        String email = myUser.getEmail();
        SocialNetworkImageVO socialNetworkImage = new SocialNetworkImageVO();
        Set<UserNotificationPreference> preferences = new HashSet<>();
        preferences.add(UserNotificationPreference.builder()
            .emailPreference(EmailPreference.SYSTEM)
            .periodicity(EmailPreferencePeriodicity.DAILY)
            .build());
        preferences.add(UserNotificationPreference.builder()
            .emailPreference(EmailPreference.LIKES)
            .periodicity(EmailPreferencePeriodicity.TWICE_A_DAY)
            .build());

        myUser.setNotificationPreferences(preferences);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(socialNetworkImageService.getSocialNetworkImageByUrl(anyString()))
            .thenReturn(socialNetworkImage);
        when(userRepo.save(myUser)).thenReturn(myUser);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);
        verify(userRepo).findByEmail(userEmail);
        verify(greenCityRemoteClient).setLocationForUser(myUser.getId(), request);
        verify(socialNetworkService).delete(anyLong());
        verify(socialNetworkImageService, times(request.getSocialNetworks().size()))
            .getSocialNetworkImageByUrl(anyString());
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullValuesTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(null).build());

        var myUser = ModelUtils.getUserWithSocialNetworks();
        String email = myUser.getEmail();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);

        String result = userService.saveUserProfile(request, email);
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail(userEmail);
        verify(greenCityRemoteClient).setLocationForUser(myUser.getId(), request);
        verify(socialNetworkService, never()).delete(anyLong());
        verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullLatitudeTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(1.0d).build());

        var myUser = ModelUtils.getUserWithSocialNetworks();
        String email = myUser.getEmail();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);

        String result = userService.saveUserProfile(request, email);
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail(userEmail);
        verify(greenCityRemoteClient).setLocationForUser(myUser.getId(), request);
        verify(socialNetworkService, never()).delete(anyLong());
        verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
        verify(userRepo).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullLongitudeTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(1.0d).longitude(null).build());

        var myUser = ModelUtils.getUserWithSocialNetworks();
        String email = myUser.getEmail();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);

        String result = userService.saveUserProfile(request, email);
        assertEquals(UpdateConstants.SUCCESS_EN, result);

        verify(userRepo).findByEmail(userEmail);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);
        verify(socialNetworkService, never()).delete(anyLong());
        verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
        verify(userRepo).save(myUser);
    }

    @ParameterizedTest
    @MethodSource("provideUserProfileTestData")
    void updateUserProfileLocationTest(User myUser, CoordinatesDto coordinates, boolean shouldCallSave,
        boolean shouldDeleteSocial, boolean shouldGetSocialImage) {
        String email = myUser.getEmail();
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmytro");
        request.setCoordinates(coordinates);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        if (shouldCallSave) {
            when(userRepo.save(myUser)).thenReturn(myUser);
        }
        String actualResult = userService.saveUserProfile(request, email);
        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);
        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).setLocationForUser(userId, request);

        if (shouldDeleteSocial) {
            verify(socialNetworkService).delete(anyLong());
        } else {
            verify(socialNetworkService, never()).delete(anyLong());
        }
        if (shouldGetSocialImage) {
            verify(socialNetworkImageService).getSocialNetworkImageByUrl(anyString());
        } else {
            verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
        }
        if (shouldCallSave) {
            verify(userRepo).save(myUser);
        } else {
            verify(userRepo, never()).save(any());
        }
    }

    private static Stream<Arguments> provideUserProfileTestData() {
        return Stream.of(
            Arguments.of(ModelUtils.getUserWithSocialNetworks(), new CoordinatesDto(20.0, 20.0), true, false, false),
            Arguments.of(ModelUtils.getUserWithUserLocation(), new CoordinatesDto(20.0, 20.0), true, false, false),
            Arguments.of(ModelUtils.getUserWithUserLocation(), new CoordinatesDto(null, null), true, false, false),
            Arguments.of(ModelUtils.getUserWithUserLocation(), new CoordinatesDto(20.0, 20.0), true, false, false),
            Arguments.of(ModelUtils.getUserWithUserLocation(), new CoordinatesDto(20.0, 20.0), true, false, false));
    }

    @Test
    void saveUserProfileThrowWrongEmailExceptionTest() {
        var request = UserProfileDtoRequest.builder().build();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        Exception thrown = assertThrows(WrongEmailException.class,
            () -> userService.saveUserProfile(request, "test@gmail.com"));
        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "test@gmail.com", thrown.getMessage());
        verify(userRepo).findByEmail(anyString());
        verify(socialNetworkService, never()).delete(anyLong());
        verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
    }

    @Test
    void saveUserProfileWhenLocationIsNotUpdatedTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var myUser = ModelUtils.getUserWithUserLocation();
        String email = myUser.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        doThrow(new GreenCityServiceException())
            .when(greenCityRemoteClient).setLocationForUser(myUser.getId(), request);

        String actualResult = userService.saveUserProfile(request, email);

        assertEquals(UpdateConstants.SUCCESS_EN, actualResult);

        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).setLocationForUser(myUser.getId(), request);
        verify(socialNetworkService, never()).delete(anyLong());
        verify(socialNetworkImageService, never()).getSocialNetworkImageByUrl(anyString());
        verify(userRepo).save(myUser);
        verify(retryableTaskService).saveRetryableTask(any(), any());
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
    void getUserProfileInformationExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.getUserProfileInformation(null));
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.checkIfTheUserIsOnline((Long) null));
    }

    @Test
    void checkIfTheUserIsOnlineByEmailExceptionTest() {
        assertThrows(WrongEmailException.class, () -> userService.checkIfTheUserIsOnline((String) null));
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
    void findUsersForManagement() {
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
        assertEquals(userManagementDtoPageableDto, userService.findUsersForManagement(pageable));
        verify(userRepo).findAll(pageable);
    }

    @Test
    void updateUserTest() {
        UserManagementUpdateDto userManagementUpdateDto = ModelUtils.getUserManagementUpdateDto();
        User excepted = user;
        excepted.setName(userManagementUpdateDto.getName());
        excepted.setEmail(userManagementUpdateDto.getEmail());
        excepted.setRole(userManagementUpdateDto.getRole());
        excepted.setUserStatus(userManagementUpdateDto.getUserStatus());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.updateUser(1L, userManagementUpdateDto);
        assertEquals(excepted, user);
    }

    @Test
    void updateUserByEmailTest() {
        UserManagementUpdateDto userManagementUpdateDto = ModelUtils.getUserManagementUpdateDto();
        String email = userManagementUpdateDto.getEmail();
        User excepted = user;
        excepted.setName(userManagementUpdateDto.getName());
        excepted.setEmail(userManagementUpdateDto.getEmail());
        excepted.setRole(userManagementUpdateDto.getRole());
        excepted.setUserStatus(userManagementUpdateDto.getUserStatus());
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.updateUser(userManagementUpdateDto);
        assertEquals(excepted, user);
    }

    @Test
    void updateUserWhenUpdateUserNameFailsRetryTaskIsSaved() {
        UserManagementUpdateDto dto = ModelUtils.getUserManagementUpdateDto();
        dto.setName("BrokenName");
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        doThrow(new WebClientRequestException(
            new IOException("fail"),
            HttpMethod.POST,
            URI.create("http://localhost/fake"),
            HttpHeaders.EMPTY)).when(greenCityRemoteClient).updateUserName(user.getId(), "BrokenName");
        userService.updateUser(1L, dto);
        verify(retryableTaskService).saveRetryableTask(
            UpdateUserNameDto.builder().id(user.getId()).name("BrokenName").build(),
            RetryableTaskType.UPDATE_USERNAME);
    }

    @Test
    void updateUserByEmailWhenUpdateUserNameFailsRetryTaskIsSaved() {
        UserManagementUpdateDto dto = ModelUtils.getUserManagementUpdateDto();
        String email = dto.getEmail();
        dto.setName("BrokenName");
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        doThrow(new WebClientRequestException(
            new IOException("fail"),
            HttpMethod.POST,
            URI.create("http://localhost/fake"),
            HttpHeaders.EMPTY)).when(greenCityRemoteClient).updateUserName(user.getId(), "BrokenName");
        userService.updateUser(dto);
        verify(retryableTaskService).saveRetryableTask(
            UpdateUserNameDto.builder().id(user.getId()).name("BrokenName").build(),
            RetryableTaskType.UPDATE_USERNAME);
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
        PageableAdvancedDto<Long> usersIdsPage = new PageableAdvancedDto<>(
            usersPage.getContent().stream()
                .map(User::getId)
                .toList(),
            usersPage.getSize(),
            0,
            1,
            0,
            false,
            false,
            true,
            true);
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
    void updateUserLanguage() {
        Language language = ModelUtils.getLanguage();
        User myUser = ModelUtils.getUser();
        myUser.setLanguage(language);

        when(languageRepo.findById(1L)).thenReturn(Optional.of(language));
        when(userRepo.findById(1L)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);
        userService.updateUserLanguage(1L, 1L);
        verify(userRepo).save(myUser);
    }

    @Test
    void updateUserLanguageNotFoundExeption() {
        Language language = ModelUtils.getLanguage();
        User myUser = ModelUtils.getUser();
        myUser.setLanguage(language);

        when(languageRepo.findById(10L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.updateUserLanguage(1L, 10L));
    }

    @Test
    void updateUserLanguageUserNotFoundExeption() {
        Language language = ModelUtils.getLanguage();
        User myUser = ModelUtils.getUser();
        myUser.setLanguage(language);

        when(languageRepo.findById(1L)).thenReturn(Optional.of(language));
        when(userRepo.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.updateUserLanguage(1L, 1L));
    }

    @Test
    void getAvailableCustomToDoListItem() {
        Long habitId = 1L;
        CustomToDoListItemResponseDto customToDoListItemResponseDto =
            new CustomToDoListItemResponseDto(1L, "test");

        when(greenCityRemoteClient.getAllAvailableCustomToDoListItems(userId, habitId))
            .thenReturn(Collections.singletonList(customToDoListItemResponseDto));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

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
                .role("1")
                .userStatus("1")
                .build();
        UserManagementVO userManagementVO =
            UserManagementVO.builder()
                .id(1L)
                .name("vivo")
                .email("test@ukr.net")
                .role(ROLE_USER)
                .userStatus(UserStatus.VERIFIED)
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
        User myUser = new User();
        myUser.setEmail(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(myUser));
        when(modelMapper.map(myUser, UserVO.class)).thenReturn(userVO);
        userService.deleteUserProfilePicture(email);
        verify(userRepo).findByEmail(email);
        verify(greenCityRemoteClient).updateUserPicturePath(myUser.getId(), null);
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
    void checkIfUserExistsByUuidTest(String uuid, boolean existence) {
        when(userRepo.existsUserByUuid(uuid)).thenReturn(existence);
        assertEquals(existence, userService.checkIfUserExistsByUuid(uuid));
    }

    private static Stream<Arguments> provideUuidOptionalUserResultForCheckIfUserExistsByUuidTest() {
        return Stream.of(
            Arguments.of("444e66e8-8daa-4cb0-8269-a8d856e7dd15", true),
            Arguments.of("uuid", false));
    }

    @Test
    void editUserRatingTest() {
        UserAddRatingDto userRatingDto2 = UserAddRatingDto.builder()
            .id(1L)
            .rating(200D)
            .build();
        UserAddRatingExternalDto userRatingExternalDto2 = UserAddRatingExternalDto.builder()
            .email(userEmail)
            .rating(200D)
            .build();

        when(userRepo.findById(userRatingDto2.getId())).thenReturn(Optional.of(user));

        userService.updateUserRating(userRatingDto2);
        verify(greenCityRemoteClient).updateUserRating(userRatingExternalDto2);
    }

    @Test
    void updateStatusTest() {
        Long id = user.getId();
        UserVOShort userVOShort = ModelUtils.getUserVOShortDto();
        userVOShort.setId(id);

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        when(modelMapper.map(user, UserVOShort.class)).thenReturn(userVOShort);

        userService.updateStatus(id, UserStatus.VERIFIED, "email");

        verify(userRepo).save(any());
    }

    @Test
    void updateStatusWithFailedCheckUpdatableUserTest() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);

        Long id = user2.getId();
        assertThrows(BadUpdateRequestException.class,
            () -> userService.updateStatus(id, UserStatus.VERIFIED, "email"));

        verify(userRepo).findByEmail(any());
        verify(modelMapper).map(user2, UserVO.class);
    }

    @Test
    void updateUserProfilePictureTest() {
        String fileName = "test.txt";
        String content = "test file content";
        String picturePath = "picturePath";
        byte[] bytes = content.getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, "text/plain", bytes);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(fileService.upload(file)).thenReturn(picturePath);
        when(modelMapper.map(any(), any())).thenReturn(userVO);

        UserVO actual = userService.updateUserProfilePicture(file, "testmail@gmail.com", null);

        assertEquals(userVO, actual);
        verify(fileService).upload(file);
        verify(modelMapper).map(any(), any());
        verify(userRepo).findByEmail(anyString());
        verify(greenCityRemoteClient).updateUserPicturePath(user.getId(), picturePath);
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
    void updateUserProfilePictureWhenCannotMapToMultipartTest() {
        String fileName = "test.txt";
        String content = "test file content";
        String picturePath = "picturePath";
        String base64 = "base64";
        String email = "testmail@gmail.com";
        byte[] bytes = content.getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, "text/plain", bytes);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(base64, MultipartFile.class)).thenThrow(new RuntimeException());

        assertThrows(
            Base64DecodedException.class,
            () -> userService.updateUserProfilePicture(file, email, base64));

        verify(userRepo).findByEmail(anyString());
        verify(modelMapper).map(base64, MultipartFile.class);
        verify(greenCityRemoteClient, never()).uploadFile(any());
        verify(greenCityRemoteClient, never()).updateUserPicturePath(user.getId(), picturePath);
    }

    @Test
    void updateUserProfilePictureWhenUpdatePathFailsRetryIsSaved() {
        var file = new MockMultipartFile("file", "name.jpg", "image/jpeg", "data".getBytes());
        var picturePath = "picturePath";

        when(userRepo.findByEmail("testmail@gmail.com")).thenReturn(Optional.of(user));
        when(fileService.upload(file)).thenReturn(picturePath);

        doThrow(new WebClientRequestException(
            new IOException("fail"),
            HttpMethod.POST,
            URI.create("http://localhost/fake"),
            HttpHeaders.EMPTY)).when(greenCityRemoteClient).updateUserPicturePath(user.getId(), picturePath);

        userService.updateUserProfilePicture(file, "testmail@gmail.com", null);

        verify(retryableTaskService).saveRetryableTask(
            UpdateUserPicturePathDto.builder()
                .userId(user.getId())
                .picturePath(picturePath)
                .build(),
            RetryableTaskType.UPDATE_USER_PICTURE_PATH);
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
    void findUserLanguageByUuidTest() {
        String uuid = "uuid";
        String languageCode = user.getLanguage().getCode();

        when(userRepo.findUserByUuid(uuid))
            .thenReturn(Optional.of(user));

        String actualResult = userService.findUserLanguageByUuid(uuid);

        assertEquals(languageCode, actualResult);
    }

    @Test
    void findUserLanguageByUuidWhenUserNotFoundTest() {
        String uuid = "uuid";

        when(userRepo.findUserByUuid(uuid))
            .thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> userService.findUserLanguageByUuid(uuid));
    }

    @Test
    void findByEmailAdvancedTest() {
        User actual = ModelUtils.getUser();

        UserVOAdvancedDto expected = getUserVOAdvancedDto();

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(actual));
        when(modelMapper.map(actual, UserVOAdvancedDto.class)).thenReturn(getUserVOAdvancedDto());

        Optional<UserVOAdvancedDto> result = userService.findByEmailAdvanced(userEmail);
        assertEquals(result.get(), expected);
        verify(userRepo, times(1)).findByEmail(userEmail);
    }

    @Test
    void createGreenCityUserTest() {
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(greenCityRemoteClient.createUser(createGreenCityUserDto)).thenReturn(true);

        assertDoesNotThrow(
            () -> userService.createGreenCityUser(user.getId(), createGreenCityUserDto.getProfilePicturePath()));
        verify(greenCityRemoteClient, times(1)).createUser(createGreenCityUserDto);
    }

    @Test
    void createGreenCityUserNotFoundTest() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.createGreenCityUser(userId, "http://anypath.com.ua"));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID, exception.getMessage());
        verifyNoInteractions(greenCityRemoteClient);
    }

    @Test
    void createGreenCityUserWhenWebClientRequestExceptionThenRetryIsSaved() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        doThrow(new WebClientRequestException(
            new IOException("fail"),
            HttpMethod.POST,
            URI.create("http://localhost/fake"),
            HttpHeaders.EMPTY)).when(greenCityRemoteClient).createUser(createGreenCityUserDto);
        String testPath = "http://testpicture.com.ua";
        userService.createGreenCityUser(userId, testPath);
        verify(retryableTaskService).saveRetryableTask(createGreenCityUserDto, RetryableTaskType.CREATE_USER);
    }

    @Test
    void createGreenCityUserWhenWebClientResponseExceptionThenOnlyWarnLogged() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        WebClientResponseException responseException = WebClientResponseException.create(
            400,
            "Bad Request",
            HttpHeaders.EMPTY,
            null,
            null);
        doThrow(responseException)
            .when(greenCityRemoteClient)
            .createUser(any(CreateGreenCityUserDto.class));
        userService.createGreenCityUser(userId, "http://testpicture.com.ua");
        verify(retryableTaskService, never()).saveRetryableTask(any(), any());
        verify(greenCityRemoteClient).createUser(any(CreateGreenCityUserDto.class));
    }

    @Test
    void createGreenCityUser_whenUnexpectedException_thenOnlyLog() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        doThrow(new RuntimeException("Unexpected error"))
            .when(greenCityRemoteClient).createUser(createGreenCityUserDto);
        String testPath = "http://testpicture.com.ua";
        userService.createGreenCityUser(userId, testPath);
        verifyNoInteractions(retryableTaskService);
    }

    @Test
    void findAllByEmailInTest() {
        List<String> emails = List.of("email1", "email2");
        User firstMockUser = new User();
        UserVO firstMockUserVO = new UserVO();
        User secondMockUser = new User();
        UserVO secondMockUserVO = new UserVO();
        List<User> userRepoResponse = List.of(firstMockUser, secondMockUser);
        List<UserVO> expectedResult = List.of(firstMockUserVO, secondMockUserVO);

        when(userRepo.findAllByEmailIn(emails))
            .thenReturn(userRepoResponse);
        when(modelMapper.map(firstMockUser, UserVO.class))
            .thenReturn(firstMockUserVO);
        when(modelMapper.map(secondMockUser, UserVO.class))
            .thenReturn(secondMockUserVO);

        List<UserVO> actualResult = userService.findAllByEmailIn(emails);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateUserRating_success_noRetry() {
        UserAddRatingExternalDto externalDto = UserAddRatingExternalDto.builder()
            .email(userEmail)
            .rating(userRatingDto.getRating())
            .build();
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userRatingDto.getId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUserRating(userRatingDto);
        verify(greenCityRemoteClient).updateUserRating(externalDto);
        verifyNoInteractions(retryableTaskService);
    }

    @Test
    void updateUserRating_webClientException_retrySaved() {
        when(userRatingDto.getId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new WebClientRequestException(
            new IOException("fail"),
            HttpMethod.POST,
            URI.create("http://localhost/fake"),
            HttpHeaders.EMPTY)).when(greenCityRemoteClient).updateUserRating(any());
        userService.updateUserRating(userRatingDto);
        verify(retryableTaskService).saveRetryableTask(userRatingDto, RetryableTaskType.UPDATE_USER_RATING);
    }

    @Test
    void updateUserRating_greenCityServiceException_retrySaved() {
        when(userRatingDto.getId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        doThrow(new GreenCityServiceException("fail")).when(greenCityRemoteClient)
            .updateUserRating(any());

        userService.updateUserRating(userRatingDto);

        verify(retryableTaskService).saveRetryableTask(userRatingDto, RetryableTaskType.UPDATE_USER_RATING);
    }

    @Test
    void findAllEmailsByIdInTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<UserEmailDto> mockDtos = List.of(
            new UserEmailDto(1L, "user1@example.com"),
            new UserEmailDto(2L, "user2@example.com"),
            new UserEmailDto(3L, "user3@example.com"));
        List<String> expectedEmails = List.of("user1@example.com", "user2@example.com", "user3@example.com");

        when(userRepo.findAllEmailsByIdIn(ids)).thenReturn(mockDtos);

        List<String> actualEmails = userService.findAllEmailsByIdIn(ids);

        assertEquals(expectedEmails, actualEmails);
        verify(userRepo).findAllEmailsByIdIn(ids);
    }

    @Test
    void verifyUserStatusTest() {
        UserVO testUser = ModelUtils.getUserVO();
        testUser.setUserStatus(UserStatus.VERIFIED);
        ProjectName projectName = ProjectName.GREENCITY;
        ServiceUserStatus status = ServiceUserStatus.ACTIVATED;

        when(greenCityRemoteClient.getGreenCityUserStatus(any())).thenReturn(status);

        assertDoesNotThrow(() -> userService.verifyUserStatus(testUser, projectName));
        verify(greenCityRemoteClient).getGreenCityUserStatus(any());
        verify(greenCityRemoteClient, never()).getUbsUserStatus(any());
    }

    @Test
    void verifyUserStatusWithNotVerifiedUserTest() {
        UserVO testUser = ModelUtils.getUserVO();
        testUser.setUserStatus(UserStatus.CREATED);

        assertThrows(BadUserStatusException.class, () -> userService.verifyUserStatus(testUser, ProjectName.PICKUP));
        verify(greenCityRemoteClient, never()).getGreenCityUserStatus(any());
        verify(greenCityRemoteClient, never()).getUbsUserStatus(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"DEACTIVATED", "BLOCKED", "DELETED"})
    void verifyUserStatusWithBadUserStatusTest(String status) {
        UserVO testUser = ModelUtils.getUserVO();
        testUser.setUserStatus(UserStatus.VERIFIED);
        ServiceUserStatus serviceStatus = ServiceUserStatus.valueOf(status);

        when(greenCityRemoteClient.getUbsUserStatus(any())).thenReturn(serviceStatus);

        assertThrows(BadUserStatusException.class, () -> userService.verifyUserStatus(testUser, ProjectName.PICKUP));
    }

    @Test
    void findByUuidTest() {
        String uuid = user.getUuid();
        when(userRepo.findUserByUuid(uuid)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);

        UserVO actualUser = userService.findByUuid(uuid);

        assertEquals(userVO, actualUser);
        verify(userRepo).findUserByUuid(uuid);
    }

    @Test
    void findByEmailShortTest() {
        UserVOShort userVOShort = ModelUtils.getUserVOShortDto();

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVOShort.class)).thenReturn(userVOShort);

        UserVOShort actualUser = userService.findByEmailShort(userEmail);

        assertEquals(userVOShort, actualUser);
        verify(userRepo).findByEmail(userEmail);
    }

    @Test
    void findByEmailShortWhenUserNotFoundTest() {
        when(userRepo.findByEmail(userEmail)).thenThrow(new NotFoundException());

        assertThrows(NotFoundException.class, () -> userService.findByEmailShort(userEmail));
        verify(userRepo).findByEmail(userEmail);
    }
}
