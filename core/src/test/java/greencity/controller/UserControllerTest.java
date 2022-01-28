package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;

import static greencity.constant.AppConstant.AUTHORIZATION;

import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.*;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.service.UserService;

import java.security.Principal;
import java.util.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    private static final String userLink = "/user";
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, new ModelMapper()))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void updateStatusTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"id\": 0,\n"
            + "  \"userStatus\": \"BLOCKED\"\n"
            + "}";

        mockMvc.perform(patch(userLink + "/status")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        UserStatusDto userStatusDto =
            mapper.readValue(content, UserStatusDto.class);

        verify(userService).updateStatus(userStatusDto.getId(),
            userStatusDto.getUserStatus(), "testmail@gmail.com");
    }

    @Test
    void updateStatusBadRequestTest() throws Exception {
        mockMvc.perform(patch(userLink + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoleTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"id\": 1,\n"
            + "  \"role\": \"ROLE_USER\"\n"
            + "}";

        mockMvc.perform(patch(userLink + "/role")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();

        verify(userService).updateRole(1L, Role.ROLE_USER, "testmail@gmail.com");
    }

    @Test
    void updateRoleBadRequestTest() throws Exception {
        mockMvc.perform(patch(userLink + "/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsersTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(userLink + "/all?page=1"))
            .andExpect(status().isOk());

        verify(userService).findByPage(pageable);
    }

    @Test
    void findUsersRecommendedFriendsTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/recommendedFriends/", 1))
            .andExpect(status().isOk());

        verify(userService).findUsersRecommendedFriends(pageable, 1L);
    }

    @Test
    void findAllUsersFriendsTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/findAll/friends/", 1))
            .andExpect(status().isOk());

        verify(userService).findAllUsersFriends(pageable, 1L);
    }

    @Test
    void findAllUsersFriendRequestTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/friendRequests/", 1))
            .andExpect(status().isOk());

        verify(userService).getAllUserFriendRequests(1L, pageable);
    }

    @Test
    void getRolesTest() throws Exception {
        mockMvc.perform(get(userLink + "/roles"))
            .andExpect(status().isOk());

        verify(userService).getRoles();
    }

    @Test
    void getEmailNotificationsTest() throws Exception {
        mockMvc.perform(get(userLink + "/emailNotifications"))
            .andExpect(status().isOk());

        verify(userService).getEmailNotificationsStatuses();
    }

    @Test
    void getUsersByFilterTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        String content = "{\n"
            + "  \"searchReg\": \"string\"\n"
            + "}";

        mockMvc.perform(post(userLink + "/filter?page=1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        FilterUserDto filterUserDto =
            mapper.readValue(content, FilterUserDto.class);

        verify(userService).getUsersByFilter(filterUserDto, pageable);
    }

    @Test
    void getUserByPrincipalTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        mockMvc.perform(get(userLink)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).getUserUpdateDtoByEmail("testmail@gmail.com");
    }

    @Test
    void updateUserTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = "{\n"
            + "  \"emailNotification\": \"DISABLED\",\n"
            + "  \"name\": \"string\"\n"
            + "}";

        ObjectMapper mapper = new ObjectMapper();
        UserUpdateDto userUpdateDto =
            mapper.readValue(content, UserUpdateDto.class);

        mockMvc.perform(patch(userLink)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(userService).update(userUpdateDto, "testmail@gmail.com");
    }

    @Test
    void getAvailableCustomShoppingListItemTest() throws Exception {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        mockMvc.perform(get(userLink + "/{userId}/custom-shopping-list-items/available", 1)
            .headers(headers))
            .andExpect(status().isOk());

        verify(userService).getAvailableCustomShoppingListItems(1L);
    }

    @Test
    void getActivatedUsersAmountTest() throws Exception {
        mockMvc.perform(get(userLink + "/activatedUsersAmount"))
            .andExpect(status().isOk());

        verify(userService).getActivatedUsersAmount();
    }

    @Test
    void updateUserProfilePictureTest() throws Exception {
        UserVO user = ModelUtils.getUserVO();
        Principal principal = mock(Principal.class);

        String json = "{\n"
            + "\t\"id\": 1,\n"
            + "\t\"profilePicturePath\": \"ima\""
            + "}";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        MockMultipartFile jsonFile = new MockMultipartFile("userProfilePictureDto", "",
            "application/json", json.getBytes());

        when(principal.getName()).thenReturn("testmail@gmail.com");
        when(userService.updateUserProfilePicture(null, "testmail@gmail.com",
            "test")).thenReturn(user);

        MockMultipartHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.multipart(userLink + "/profilePicture");
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        this.mockMvc.perform(builder
            .file(jsonFile)
            .headers(headers)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteUserProfilePictureTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@email.com");
        mockMvc.perform(patch(userLink + "/deleteProfilePicture")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserProfilePicture("test@email.com");
    }

    @Test
    void deleteUserFriendTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/userFriend/{friendId}", 1, 1))
            .andExpect(status().isOk());

        verify(userService).deleteUserFriendById(1L, 1L);
    }

    @Test
    void addNewFriendTest() throws Exception {
        mockMvc.perform(post(userLink + "/{userId}/userFriend/{friendId}", 1, 1))
            .andExpect(status().isOk());

        verify(userService).addNewFriend(1L, 1L);
    }

    @Test
    void acceptFriendRequestTest() throws Exception {
        mockMvc.perform(post(userLink + "/{userId}/acceptFriend/{friendId}", 1, 2))
            .andExpect(status().isOk());

        verify(userService).acceptFriendRequest(1L, 2L);
    }

    @Test
    void declineFriendRequestTest() throws Exception {
        mockMvc.perform(delete(userLink + "/{userId}/declineFriend/{friendId}", 1, 2))
            .andExpect(status().isOk());

        verify(userService).declineFriendRequest(1L, 2L);
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/sixUserFriends/", 1))
            .andExpect(status().isOk());

        verify(userService).getSixFriendsWithTheHighestRatingPaged(1L);
    }

    @Test
    void getUserProfileInformationTest() throws Exception {
        mockMvc.perform(get(userLink + "/{userId}/profile/", 1))
            .andExpect(status().isOk());
        verify(userService).getUserProfileInformation(1L);
    }

    @Test
    void checkIfTheUserIsOnlineTest() throws Exception {
        mockMvc.perform(get(userLink + "/isOnline/{userId}/", 1))
            .andExpect(status().isOk());
        verify(userService).checkIfTheUserIsOnline(1L);
    }

    @Test
    void getUserProfileStatistics() throws Exception {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        mockMvc.perform(get(userLink + "/{userId}/profileStatistics/", 1)
            .headers(headers))
            .andExpect(status().isOk());
        verify(userService).getUserProfileStatistics((1L));
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testName");

        String json = "{\n"
            + "\t\"name\": \"testName\",\n"
            + "\t\"city\": \"city\",\n"
            + "\t\"userCredo\": \"credo\",\n"
            + "\t\"socialNetworks\": [],\n"
            + "\t\"showLocation\": true,\n"
            + "\t\"showEcoPlace\": true,\n"
            + "\t\"showShoppingList\": false\n"
            + "}";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);

        this.mockMvc.perform(put(userLink + "/profile")
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .param("accessToken", "accessToken")
            .principal(principal))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        UserProfileDtoRequest dto = mapper.readValue(json, UserProfileDtoRequest.class);

        verify(userService).saveUserProfile(dto, "testName");
    }

    @Test
    void searchTest() throws Exception {
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
        String content = objectMapper.writeValueAsString(userViewDto);
        List<UserManagementVO> userManagementVOS = Collections.singletonList(new UserManagementVO());
        PageableAdvancedDto<UserManagementVO> userAdvancedDto =
            new PageableAdvancedDto<>(userManagementVOS, 20, 0, 0, 0,
                true, true, true, true);
        when(userService.search(pageable, userViewDto)).thenReturn(userAdvancedDto);
        mockMvc.perform(post(userLink + "/search")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userService).search(pageable, userViewDto);
    }

    @Test
    void findByEmailTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(userVO);
        mockMvc.perform(get(userLink + "/findByEmail")
            .param("email", TestConst.EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(TestConst.NAME))
            .andExpect(jsonPath("$.email").value(TestConst.EMAIL));
    }

    @Test
    void findByIdTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findById(1L)).thenReturn(userVO);
        mockMvc.perform(get(userLink + "/findById")
            .param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(TestConst.NAME))
            .andExpect(jsonPath("$.email").value(TestConst.EMAIL));
    }

    @Test
    void findUserForAchievementTest() throws Exception {
        UserVOAchievement userVOAchievement = UserVOAchievement.builder()
            .id(1L)
            .name(TestConst.NAME)
            .userAchievements(List.of(
                UserAchievementVO.builder()
                    .id(10L)
                    .user(ModelUtils.getUserVO())
                    .achievement(AchievementVO.builder()
                        .id(20L)
                        .achievementCategory(AchievementCategoryVO.builder()
                            .id(30L)
                            .name("TestAchievementCategory")
                            .build())
                        .build())
                    .build()))
            .build();
        when(userService.findUserForAchievement(1L)).thenReturn(userVOAchievement);
        mockMvc.perform(get(userLink + "/findByIdForAchievement")
            .param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(TestConst.NAME))
            .andExpect(jsonPath("$.userAchievements.length()").value(1));
    }

    @Test
    void findUserForManagementTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        when(userService.findUserForManagementByPage(pageable)).thenReturn(ModelUtils.getPageableAdvancedDto());
        mockMvc.perform(get(userLink + "/findUserForManagement"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1L))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void searchByTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        String query = "testQuery";
        when(userService.searchBy(pageable, query)).thenReturn(ModelUtils.getPageableAdvancedDto());
        mockMvc.perform(get(userLink + "/searchBy")
            .param("query", query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1L))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void updateUserManagementTest() throws Exception {
        String content = objectMapper.writeValueAsString(ModelUtils.getUserManagementDto());
        mockMvc.perform(put(userLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
        verify(userService).updateUser(ModelUtils.getUserManagementDto());
    }

    @Test
    void updateUserManagementBadRequestTest() throws Exception {
        mockMvc.perform(put(userLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
        verify(userService, times(0)).updateUser(ModelUtils.getUserManagementDto());
    }

    @Test
    void findAllTest() throws Exception {
        when(userService.findAll()).thenReturn(List.of(ModelUtils.getUserVO()));
        mockMvc.perform(get(userLink + "/findAll"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value(TestConst.NAME))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].email").value(TestConst.EMAIL));
    }

    @Test
    void findUserFriendByUserIdTest() throws Exception {
        when(userService.findUserFriendsByUserId(1L)).thenReturn(List.of(ModelUtils.getUserManagementDto()));
        mockMvc.perform(get(userLink + "/1/friends"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value(TestConst.NAME))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].email").value(TestConst.EMAIL))
            .andExpect(jsonPath("$[0].userCredo").value(TestConst.CREDO));
    }

    @Test
    void findNotDeactivatedByEmailTest() throws Exception {
        when(userService.findNotDeactivatedByEmail(TestConst.EMAIL)).thenReturn(Optional.of(ModelUtils.getUserVO()));
        mockMvc.perform(get(userLink + "/findNotDeactivatedByEmail")
            .param("email", TestConst.EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(TestConst.NAME))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value(TestConst.EMAIL));
    }

    @Test
    void findNotDeactivatedByEmailIfNullTest() throws Exception {
        when(userService.findNotDeactivatedByEmail(TestConst.EMAIL)).thenReturn(Optional.empty());
        mockMvc.perform(get(userLink + "/findNotDeactivatedByEmail")
            .param("email", TestConst.EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void createUbsRecordTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(TestConst.EMAIL);
        when(userService.findByEmail(principal.getName())).thenReturn(ModelUtils.getUserVO());

        when(userService.createUbsRecord(ModelUtils.getUserVO())).thenReturn(UbsTableCreationDto.builder()
            .uuid("testUuid")
            .build());
        mockMvc.perform(get(userLink + "/createUbsRecord")
            .principal(principal)
            .content(objectMapper.writeValueAsString(ModelUtils.getUserVO())))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.uuid").value("testUuid"));

    }

    @Test
    void findIdByEmailTest() throws Exception {
        when(userService.findIdByEmail(TestConst.EMAIL)).thenReturn(1L);
        mockMvc.perform(get(userLink + "/findIdByEmail")
            .param("email", TestConst.EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @Test
    void findUuidByEmailTest() throws Exception {
        when(userService.findUuIdByEmail(TestConst.EMAIL)).thenReturn(TestConst.UUID);
        mockMvc.perform(get(userLink + "/findUuidByEmail")
            .param("email", TestConst.EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(TestConst.UUID));
    }

    @Test
    void updateUserLanguageTest() throws Exception {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        mockMvc.perform(put(userLink + "/{userId}/language/{languageId}", 1, 1)
            .headers(headers))
            .andExpect(status().isOk());
        verify(userService).updateUserLanguage(1L, 1L);
    }

    @Test
    void getUserLang() throws Exception {
        this.mockMvc.perform(get(userLink + "/lang" + "?id=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userService).getUserLang(1L);
    }

    @Test
    void getReasonsOfDeactivation() throws Exception {
        List<String> test = List.of("test", "test");
        when(userService.getDeactivationReason(1L, "en")).thenReturn(test);
        this.mockMvc.perform(get(userLink + "/reasons" + "?id=1" + "&admin=en")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userService).getDeactivationReason(1L, "en");
    }

    @Test
    void deactivateAllUserTest() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L, 4L);
        when(userService.deactivateAllUsers(ids)).thenReturn(ids);
        mockMvc.perform(put(userLink + "/deactivateAll")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4));

    }

    @Test
    void saveUserTest() throws Exception {
        when(userService.save(ModelUtils.getUserVO())).thenReturn(ModelUtils.getUserVO());
        mockMvc.perform(post(userLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ModelUtils.getUserVO())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(TestConst.NAME))
            .andExpect(jsonPath("$.email").value(TestConst.EMAIL));
    }

    @Test
    void findAllByEmailNotificationTest() throws Exception {
        EmailNotification notification = EmailNotification.DAILY;
        when(userService.findAllByEmailNotification(notification))
            .thenReturn(List.of(ModelUtils.getUserVO()));
        mockMvc.perform(get(userLink + "/findAllByEmailNotification")
            .param("emailNotification", notification.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1L));

    }

    @Test
    void scheduleDeleteDeactivateUserTest() throws Exception {
        when(userService.scheduleDeleteDeactivatedUsers()).thenReturn(1);

        mockMvc.perform(post(userLink + "/deleteDeactivatedUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @Test
    void findAllUsersCitiesTest() throws Exception {
        List<String> cities = List.of("Lviv", "Kyiv", "Kharkiv");
        when(userService.findAllUsersCities()).thenReturn(cities);
        mockMvc.perform(get(userLink + "/findAllUsersCities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$", Matchers.containsInAnyOrder("Lviv", "Kyiv", "Kharkiv")));
    }

    @Test
    void findAllRegistrationMonthsMapTest() throws Exception {
        Map<Integer, Long> map = new HashMap<>();
        map.put(1, 10L);
        map.put(12, 20L);
        when(userService.findAllRegistrationMonthsMap()).thenReturn(map);
        mockMvc.perform(get(userLink + "/findAllRegistrationMonthsMap"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.1").value(10))
            .andExpect(jsonPath("$.12").value(20));
    }

    @Test
    void findNewFriendsByNameTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(TestConst.EMAIL);
        when(userService.findByEmail(principal.getName())).thenReturn(ModelUtils.getUserVO());
        mockMvc.perform(get(userLink + "/findNewFriendsByName?page=" + pageNumber + "&name=test")
            .principal(principal)).andExpect(status().isOk());

        verify(userService).findNewFriendByName("test", pageable, 1L);
    }

    @Test
    void findFriendsByName() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(TestConst.EMAIL);
        when(userService.findByEmail(principal.getName())).thenReturn(ModelUtils.getUserVO());
        mockMvc.perform(get(userLink + "/findFriendByName?page=" + pageNumber + "&name=test")
            .principal(principal)).andExpect(status().isOk());

        verify(userService).findFriendByName("test", pageable, 1L);
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriend() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(userLink + "/{userId}/findAll/friendsWithoutExist/", 1))
            .andExpect(status().isOk());

        verify(userService).findAllUsersExceptMainUserAndUsersFriend(pageable, 1L);
    }

    @Test
    void deactivateUser() throws Exception {
        String uuid = "87df9ad5-6393-441f-8423-8b2e770b01a8";
        List<String> uuids = List.of("uuid5", "uuid3");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@email.com");

        mockMvc.perform(put(userLink + "/markUserAsDeactivated" + "?uuid=" + uuid)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(uuids)))
            .andExpect(status().isOk());
        verify(userService).markUserAsDeactivated(uuid);
    }
}
