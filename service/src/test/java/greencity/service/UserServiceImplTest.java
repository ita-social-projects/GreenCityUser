package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.friends.SixFriendsPageResponceDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.user.RecommendedFriendDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserWithOnlineStatusDto;
import greencity.dto.user.UsersFriendDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.CheckRepeatingValueException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.DEACTIVATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    private User user = User.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastVisit(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();

    private UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastVisit(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();
    private User user2 = User.builder()
        .id(2L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_MODERATOR)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastVisit(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .build();
    private UserVO userVO2 =
        UserVO.builder()
            .id(2L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(Role.ROLE_MODERATOR)
            .userStatus(ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
            .dateOfRegistration(LocalDateTime.now())
            .build();

    private String language = "ua";
    private Long userId = user.getId();
    private Long userId2 = user2.getId();
    private String userEmail = user.getEmail();

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findUsersRecommendedFriendsTest() {
        List<UsersFriendDto> singletonList = Collections.singletonList(ModelUtils.usersFriendDto);
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<UsersFriendDto> page = new PageImpl<>(singletonList, pageRequest, singletonList.size());
        List<RecommendedFriendDto> dtoList =
            Collections.singletonList(ModelUtils.getRecommendedFriendDto());
        PageableDto<RecommendedFriendDto> pageableDto =
            new PageableDto<>(dtoList, dtoList.size(), 0, 1);
        when(userRepo.findUsersRecommendedFriends(pageRequest, userId)).thenReturn(page);
        when(modelMapper.map(singletonList.get(0), RecommendedFriendDto.class)).thenReturn(dtoList.get(0));
        PageableDto<RecommendedFriendDto> actual = userService.findUsersRecommendedFriends(pageRequest, 1L);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findAllUsersFriendsTest() {
        List<User> singletonList = Collections.singletonList(ModelUtils.getUser());
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<User> page = new PageImpl<>(singletonList, pageRequest, singletonList.size());
        List<RecommendedFriendDto> dtoList =
            Collections.singletonList(ModelUtils.getRecommendedFriendDto());
        PageableDto<RecommendedFriendDto> pageableDto =
            new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(userRepo.getAllUserFriends(userId, pageRequest)).thenReturn(page);
        when(modelMapper.map(singletonList, new TypeToken<List<RecommendedFriendDto>>() {
        }.getType())).thenReturn(dtoList);
        PageableDto<RecommendedFriendDto> actual = userService.findAllUsersFriends(pageRequest, 1L);

        assertEquals(pageableDto, actual);
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
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setRole(Role.ROLE_MODERATOR);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(modelMapper.map(Optional.of(user2), UserVO.class)).thenReturn(userVO2);
        user.setRole(Role.ROLE_MODERATOR);
        when(userRepo.save(any())).thenReturn(user);
        when(modelMapper.map(user, UserRoleDto.class)).thenReturn(userRoleDto);
        assertEquals(
            Role.ROLE_MODERATOR,
            userService.updateRole(userId, Role.ROLE_MODERATOR, any()).getRole());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    void updateRoleOnTheSameUserTest() {
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertThrows(BadUpdateRequestException.class, () -> userService.updateRole(userId, null, userEmail));
    }

    @Test
    void findByIdTest() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(userVO, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(WrongIdException.class);
        assertThrows(WrongIdException.class, () -> userService.findById(1L));
    }

    @Test
    void deleteByIdExceptionBadIdTest() {
        assertThrows(WrongIdException.class, () -> userService.deleteById(1L));
    }

    @Test
    void deleteByNullIdExceptionTest() {
        when(userRepo.findById(1L)).thenThrow(new WrongIdException(""));
        assertThrows(WrongIdException.class, () -> userService.deleteById(1L));
    }

    @Test
    void deleteByExistentIdTest() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        userService.deleteById(userId);
        verify(userRepo).delete(user);
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
    void findByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = new User();
        user.setName("Roman Romanovich");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Romanovich");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
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
        RoleDto roleDto = new RoleDto(Role.class.getEnumConstants());
        assertEquals(roleDto, userService.getRoles());
    }

    @Test
    void getEmailStatusesTest() {
        List<EmailNotification> placeStatuses =
            Arrays.asList(EmailNotification.class.getEnumConstants());

        assertEquals(placeStatuses, userService.getEmailNotificationsStatuses());
    }

    @Test
    void updateLastVisit() {
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(userRepo.save(any())).thenReturn(user);
        LocalDateTime localDateTime = user.getLastVisit().minusHours(1);
        assertNotEquals(localDateTime, userService.updateLastVisit(userVO).getLastVisit());
    }

    @Test
    void getUsersByFilter() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = new User();
        user.setName("Roman Bezos");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Bezos");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
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
    void getProfilePicturePathByUserIdNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.getProfilePicturePathByUserId(1L));
    }

    @Test
    void getProfilePicturePathByUserIdTest() {
        when(userRepo.getProfilePicturePathByUserId(1L)).thenReturn(Optional.of(anyString()));
        userService.getProfilePicturePathByUserId(1L);
        verify(userRepo).getProfilePicturePathByUserId(1L);
    }

    @Test
    void updateUserProfilePictureNotUpdatedExceptionTest() {
        UserProfilePictureDto userProfilePictureDto = ModelUtils.getUserProfilePictureDto();
        userProfilePictureDto.setProfilePicturePath(null);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class,
            () -> userService.updateUserProfilePicture(null, "testmail@gmail.com",
                userProfilePictureDto, "accessToken"));
    }

    @Test
    void deleteUserFriendByIdCheckRepeatingValueExceptionTest() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(CheckRepeatingValueException.class, () -> userService.deleteUserFriendById(1L, 1L));
    }

    @Test
    void deleteUserFriendByIdTest() {
        List<User> list = Collections.singletonList(user2);
        List<UserVO> listVO = Collections.singletonList(userVO2);
        when(userRepo.getAllUserFriends(anyLong())).thenReturn(list);
        when(modelMapper.map(list,
            new TypeToken<List<UserVO>>() {
            }.getType())).thenReturn(listVO);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        userService.deleteUserFriendById(userId, user2.getId());
        verify(userRepo).deleteUserFriendById(userId, user2.getId());
    }

    @Test
    void deleteUserFriendByIdNotDeletedExceptionTest() {
        when(userRepo.getAllUserFriends(1L)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(),
            new TypeToken<List<UserVO>>() {
            }.getType())).thenReturn(Collections.emptyList());
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        assertThrows(NotDeletedException.class, () -> userService.deleteUserFriendById(1L, 2L));
    }

    @Test
    void deleteUserFriendByIdNotDeletedExceptionTest2() {
        when(userRepo.getAllUserFriends(any())).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(Collections.singletonList(user),
            new TypeToken<List<UserVO>>() {
            }.getType())).thenReturn(Collections.singletonList(userVO));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        assertThrows(NotDeletedException.class, () -> userService.deleteUserFriendById(3L, 2L));
    }

    @Test
    void addNewFriendCheckRepeatingValueExceptionWithSameIdTest() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(CheckRepeatingValueException.class, () -> userService.addNewFriend(1L, 1L));
    }

    @Test
    void addNewFriendCheckRepeatingValueExceptionWithSameIdTest2() {
        when(userRepo.getAllUserFriends(any())).thenReturn(Collections.singletonList(user2));
        when(modelMapper.map(Collections.singletonList(user2),
            new TypeToken<List<UserVO>>() {
            }.getType())).thenReturn(Collections.singletonList(userVO2));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        assertThrows(CheckRepeatingValueException.class, () -> userService.addNewFriend(1L, 2L));
    }

    @Test
    void addNewFriendTest() {
        when(userRepo.getAllUserFriends(any())).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(),
            new TypeToken<List<UserVO>>() {
            }.getType())).thenReturn(Collections.emptyList());
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));
        userService.addNewFriend(1L, 2L);
        verify(userRepo).addNewFriend(1L, 2L);
    }

    @Test
    void getSixFriendsWithTheHighestRatingExceptionTest() {
        assertThrows(NotFoundException.class, () -> userService.getSixFriendsWithTheHighestRating(1L));
    }

    @Test
    void getSixFriendsWithTheHighestRatingPagedTest() {
        Pageable pageable = PageRequest.of(0, 6);
        List<User> users = Collections.singletonList(ModelUtils.getUser());
        Page<User> pageUsers = new PageImpl<>(users, pageable, users.size());
        List<UserProfilePictureDto> userProfilePictureDtoList =
            Collections.singletonList(ModelUtils.getUserProfilePictureDto());

        when(userRepo.getSixFriendsWithTheHighestRating(anyLong())).thenReturn(users);
        when(modelMapper.map(users.get(0), UserProfilePictureDto.class)).thenReturn(userProfilePictureDtoList.get(0));
        when(userRepo.getAllUserFriendsCount(anyLong())).thenReturn(5);

        SixFriendsPageResponceDto expected = SixFriendsPageResponceDto.builder()
            .pagedFriends(new PageableDto<>(
                userProfilePictureDtoList,
                pageUsers.getTotalElements(),
                pageUsers.getPageable().getPageNumber(),
                pageUsers.getTotalPages()))
            .amountOfFriends(5).build();

        assertEquals(expected, userService.getSixFriendsWithTheHighestRatingPaged(10L));
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        UserProfilePictureDto e = new UserProfilePictureDto();
        List<UserProfilePictureDto> list = Collections.singletonList(e);
        when(userRepo.getSixFriendsWithTheHighestRating(1L)).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserProfilePictureDto.class)).thenReturn(e);
        assertEquals(list, userService.getSixFriendsWithTheHighestRating(1L));
    }

    @Test
    void getUserProfileInformationTest() {
        UserProfileDtoResponse response = new UserProfileDtoResponse();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDtoResponse.class)).thenReturn(response);
        assertEquals(response, userService.getUserProfileInformation(1L));
    }

    @Test
    void getUserProfileInformationExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.getUserProfileInformation(null));
    }

    @Test
    void updateUserLastActivityTimeTest() {
        Date currentTime = new Date();
        userService.updateUserLastActivityTime(userId, currentTime);
        verify(userRepo).updateUserLastActivityTime(userId, currentTime);
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.checkIfTheUserIsOnline(null));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));

        assertTrue(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        LocalDateTime localDateTime = LocalDateTime.of(
            2015, Month.JULY, 29, 19, 30, 40);
        Timestamp userLastActivityTime = Timestamp.valueOf(localDateTime);
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.checkIfTheUserIsOnline(1L));
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
                .map(user -> modelMapper.map(user, UserManagementDto.class))
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
    }

    @Test
    void updateUser() {
        UserManagementDto userManagementDto = new UserManagementDto();
        userManagementDto.setId(1L);
        userManagementDto.setName(user.getName());
        userManagementDto.setRole(user.getRole());
        userManagementDto.setUserStatus(user.getUserStatus());
        userManagementDto.setEmail(user.getEmail());
        userManagementDto.setUserCredo(user.getUserCredo());
        User excepted = user;
        excepted.setName(userManagementDto.getName());
        excepted.setEmail(userManagementDto.getEmail());
        excepted.setRole(userManagementDto.getRole());
        excepted.setUserCredo(userManagementDto.getUserCredo());
        excepted.setUserStatus(userManagementDto.getUserStatus());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.updateUser(userManagementDto);
        assertEquals(excepted, user);
    }

    @Test
    void findNotDeactivatedByEmail() {
        String email = "test@gmail.com";
        user.setEmail(email);
        when(userRepo.findNotDeactivatedByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(Optional.of(user), UserVO.class)).thenReturn(userVO);
        assertEquals(Optional.of(userVO), userService.findNotDeactivatedByEmail(email));
    }

    @Test
    void getUserAndSixFriendsWithOnlineStatus() {
        List<UserWithOnlineStatusDto> sixFriendsWithOnlineStatusDtos = new ArrayList<>();
        sixFriendsWithOnlineStatusDtos = Collections.singletonList(user)
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(userRepo.getSixFriendsWithTheHighestRating(userId)).thenReturn(Collections.singletonList(user));
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
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(true)
            .build();
        List<UserWithOnlineStatusDto> friendsWithOnlineStatusDtos = new ArrayList<>();
        friendsWithOnlineStatusDtos = usersPage
            .getContent()
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());
        UserAndAllFriendsWithOnlineStatusDto userAndAllFriendsWithOnlineStatusDto =
            new UserAndAllFriendsWithOnlineStatusDto().builder()
                .user(userWithOnlineStatusDto)
                .friends(new PageableDto<>(friendsWithOnlineStatusDtos, usersPage.getTotalElements(),
                    usersPage.getPageable().getPageNumber(), usersPage.getTotalPages()))
                .build();

        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(userRepo.getAllUserFriends(userId, pageable)).thenReturn(usersPage);

        assertEquals(userAndAllFriendsWithOnlineStatusDto,
            userService.getAllFriendsWithTheOnlineStatus(userId, pageable));
    }

    @Test
    void deactivateUser() {
        User expecteduUser = user;
        expecteduUser.setUserStatus(DEACTIVATED);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.deactivateUser(userId);
        assertEquals(expecteduUser, user);
    }

    @Test
    void deactivateAllUsers() {
        List<Long> longList = List.of(1L, 2L);
        assertEquals(longList, userService.deactivateAllUsers(longList));
    }

    @Test
    void setActivatedStatus() {
        User expecteduUser = user;
        user.setUserStatus(DEACTIVATED);
        expecteduUser.setUserStatus(ACTIVATED);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        userService.setActivatedStatus(userId);
        assertEquals(expecteduUser, user);
    }

    @Test
    void findByIdAndToken() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setId(2L);
        verifyEmail.setExpiryDate(LocalDateTime.now());
        verifyEmail.setToken("test");
        verifyEmail.setUser(user2);
        user2.setVerifyEmail(verifyEmail);

        when(userRepo.findById(userId2)).thenReturn(Optional.of(user2));
        when(modelMapper.map(Optional.of(user2), UserVO.class)).thenReturn(userVO2);
        when(modelMapper.map(userVO2, User.class)).thenReturn(user2);
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);

        assertEquals(Optional.of(userVO2), userService.findByIdAndToken(userId2, "test"));
    }

    @Test
    void findByIdAndToken2() {
        when(userRepo.findById(userId2)).thenReturn(Optional.of(user2));
        when(modelMapper.map(Optional.of(user2), UserVO.class)).thenReturn(userVO2);
        when(modelMapper.map(userVO2, User.class)).thenReturn(user2);
        when(modelMapper.map(user2, UserVO.class)).thenReturn(userVO2);
        assertEquals(Optional.empty(), userService.findByIdAndToken(userId2, "test"));
    }

    @Test
    void getAvailableCustomGoals() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "test");
        when(restClient.getAllAvailableCustomGoals(userId, entity))
            .thenReturn(Collections.singletonList(customGoalResponseDto));

        assertEquals(Collections.singletonList(customGoalResponseDto),
            userService.getAvailableCustomGoals(userId, accessToken));
    }
}
