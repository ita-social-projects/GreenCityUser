package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.AppConstant;
import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.converters.UserArgumentResolver;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.language.LanguageVO;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.security.service.AuthorityService;
import greencity.security.service.PositionService;
import greencity.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    private static final String userLink = "/user";
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private AuthorityService authorityService;
    @Mock
    private PositionService positionService;
    private ObjectMapper objectMapper;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void updateStatusTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = """
            {
              "id": 0,
              "userStatus": "BLOCKED"
            }\
            """;

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
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = """
            {
              "role": "ROLE_USER"
            }\
            """;

        mockMvc.perform(patch(userLink + "/1/role")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(userService).updateRole(1L, Role.ROLE_USER, "testmail@gmail.com");
    }

    @Test
    void updateEmployeeEmailTest() throws Exception {
        mockMvc.perform(put(userLink + "/employee-email")
            .param("newEmployeeEmail", TestConst.EMAIL)
            .param("uuid", TestConst.UUID))
            .andExpect(status().isOk());
        verify(userService).updateEmployeeEmail("taras@gmail.com", "TarasUUID");
    }

    @Test
    void updateRoleBadRequestForEmptyBodyTest() throws Exception {
        mockMvc.perform(patch(userLink + "/1/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
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
    void getRolesTest() throws Exception {
        Long id = 1L;
        mockMvc.perform(get(userLink + "/roles")
            .param("id", String.valueOf(id)))
            .andExpect(status().isOk());

        verify(userService).getRoles(id);
    }

    @Test
    void getEmailNotificationsTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        mockMvc.perform(get(userLink + "/emailNotifications")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).getEmailNotificationsStatuses("testmail@gmail.com");
    }

    @Test
    void getUsersByFilterTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        String content = """
            {
              "searchReg": "string"
            }\
            """;

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

        String content = """
            {
              "emailNotification": "DISABLED",
              "name": "String"
            }\
            """;

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
        mockMvc.perform(get(userLink + "/{userId}/{habitId}/custom-shopping-list-items/available", 1, 1)
            .headers(headers))
            .andExpect(status().isOk());

        verify(userService).getAvailableCustomShoppingListItems(1L, 1L);
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

        String json = """
            {
            	"id": 1,
            	"profilePicturePath": "ima"\
            }\
            """;
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
        when(principal.getName()).thenReturn("Vovka");

        String json = """
            {
            	"name": "Vovka",
            	"userCredo": "credo",
            	"socialNetworks": [],
            	"showLocation": true,
            	"showEcoPlace": true,
            	"showShoppingList": false,
            	"coordinates":{\s
             \
            	"latitude": 20.000000,
            	"longitude": 20.000000
            	}
            }\
            """;
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

        verify(userService).saveUserProfile(dto, "Vovka");
    }

    @Test
    void saveWithEmailPreferencesTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("Vovka");

        String json = """
            {
                "name": "Vovka",
                "userCredo": "credo",
                "socialNetworks": [],
                "showLocation": true,
                "showEcoPlace": true,
                "showShoppingList": false,
                "coordinates": {
                    "latitude": 20.000000,
                    "longitude": 20.000000
                },
                "emailPreferences": [
                    "LIKES",
                    "SYSTEM"
                ]
            }
            """;
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

        verify(userService).saveUserProfile(dto, "Vovka");
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
            .andExpect(content().contentType("application/json"))
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
        UserManagementUpdateDto userManagementDto = ModelUtils.getUserManagementUpdateDto();
        String content = objectMapper.writeValueAsString(userManagementDto);
        mockMvc.perform(put(userLink + "/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    void updateUserManagementBadRequestTest() throws Exception {
        mockMvc.perform(put(userLink + "/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
        verify(userService, times(0)).updateUser(1L, ModelUtils.getUserManagementUpdateDto());
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
        Principal principal = mock(Principal.class);
        String languageCode = AppConstant.DEFAULT_LANGUAGE_CODE;
        long userId = 1L;
        UserVO userVO = UserVO.builder()
            .id(userId)
            .languageVO(LanguageVO.builder()
                .id(2L)
                .code(languageCode)
                .build())
            .build();

        when(principal.getName()).thenReturn(TestConst.EMAIL);
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(put(userLink + "/language/{languageId}", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).updateUserLanguage(userId, 1L);
    }

    @Test
    void getUserLang() throws Exception {
        Principal principal = mock(Principal.class);
        String languageCode = AppConstant.DEFAULT_LANGUAGE_CODE;
        UserVO userVO = ModelUtils.TEST_USER_VO;
        userVO.setLanguageVO(LanguageVO.builder()
            .id(2L)
            .code(languageCode)
            .build());

        when(principal.getName()).thenReturn(TestConst.EMAIL);
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        this.mockMvc.perform(get(userLink + "/lang" + "?id=1")
            .principal(principal))
            .andExpect(content().string(languageCode))
            .andExpect(status().isOk());
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
    void activateUser() throws Exception {
        String uuid = "87df9ad5-6393-441f-8423-8b2e770b01a8";
        List<String> uuids = List.of("uuid5", "uuid3");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@email.com");

        mockMvc.perform(put(userLink + "/markUserAsActivated" + "?uuid=" + uuid)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(uuids)))
            .andExpect(status().isOk());
        verify(userService).markUserAsActivated(uuid);
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
        Principal principal = mock(Principal.class);
        UserCityDto userCityDto = new UserCityDto(1L, "Lviv", "Львів", 49.842957, 24.031111);
        when(userService.findByEmail(principal.getName())).thenReturn(ModelUtils.getUserVO());
        when(userService.findAllUsersCities(1L)).thenReturn(userCityDto);
        mockMvc.perform(get(userLink + "/findAllUsersCities")
            .principal(principal))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.cityEn").value("Lviv"))
            .andExpect(jsonPath("$.cityUa").value("Львів"))
            .andExpect(jsonPath("$.latitude").value(49.842957))
            .andExpect(jsonPath("$.longitude").value(24.031111));
        verify(userService).findAllUsersCities(1L);
        verify(userService).findByEmail(principal.getName());
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

    @Test
    void getAllAuthoritiesTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        mockMvc.perform(get(userLink + "/get-all-authorities" + "?email=" + principal.getName())
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(principal.getName())))
            .andExpect(status().isOk());

        verify(authorityService).getAllEmployeesAuthorities(principal.getName());
    }

    @Test
    void getPositionsAndRelatedAuthoritiesTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        mockMvc.perform(get(userLink + "/get-positions-authorities" + "?email=" + principal.getName())
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(principal.getName())))
            .andExpect(status().isOk());

        verify(positionService).getPositionsAndRelatedAuthorities(principal.getName());
    }

    @Test
    void editAuthoritiesTest() throws Exception {
        Principal principal = mock(Principal.class);
        List<String> list = new ArrayList<>();
        list.add("EDIT_ORDER");
        UserEmployeeAuthorityDto dto = UserEmployeeAuthorityDto.builder()
            .employeeEmail("test@mail.com")
            .authorities(list)
            .build();
        when(principal.getName()).thenReturn("testmail@gmail.com");

        String content = """
            {
              "authorities":[ "EDIT_ORDER"],
              "employeeEmail": "test@mail.com"
            }\
            """;

        mockMvc.perform(put(userLink + "/edit-authorities")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(authorityService).updateEmployeesAuthorities(dto);
    }

    @Test
    void updatePositionsAndRelatedAuthoritiesTest() throws Exception {
        Principal principal = mock(Principal.class);
        var dto = new EmployeePositionsDto();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(put(userLink + "/authorities")
            .principal(principal)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(authorityService).updateAuthoritiesToRelatedPositions(dto);
    }

    @Test
    void deactivateEmployeeByUUID() throws Exception {
        String uuid = "87df9ad5-6393-441f-8423-8b2e770b01a8";
        mockMvc.perform(put(userLink + "/deactivate-employee").param("uuid", uuid))
            .andExpect(status().isOk());
        verify(userService).markUserAsDeactivated(uuid);
    }

    @Test
    void checkIfUserExistsByUuidTest() throws Exception {
        when(userService.checkIfUserExistsByUuid(TestConst.UUID)).thenReturn(true);
        mockMvc.perform(get(userLink + "/checkByUuid")
            .param("uuid", TestConst.UUID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));
    }

    @Test
    void editUserRatingTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testmail@gmail.com");

        UserAddRatingDto dto = UserAddRatingDto.builder()
            .id(1L)
            .rating(10.0)
            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(userLink + "/user-rating")
            .principal(principal)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).updateUserRating(dto);
    }

    @Test
    void checkUsersOnlineStatusTest() {
        var request = new UsersOnlineStatusRequestDto();
        userController.checkUsersOnlineStatus(request);
        verify(userService).checkUsersOnlineStatus(request);
    }
}
