package greencity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.todolist.CustomToDoListItemResponseDto;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVOShort;
import greencity.enums.ServiceUserStatus;
import greencity.service.UserService;
import java.util.UUID;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GreenCityRemoteClientTest {
    static MockWebServer mockWebServer;
    GreenCityRemoteClient greenCityRemoteClient;
    UserService userService;
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    UserVOShort user = ModelUtils.getUserVOShort();
    String userEmailQueryParam = "email";
    String profilePicturePathQueryParam = "profilePicturePath";
    String userNameQueryParam = "userName";
    String pageQueryParam = "page";
    String sizeQueryParam = "size";
    String userEmailsQueryParam = "emails";
    Long userId = TestConst.USER_ID;
    String userEmail = TestConst.EMAIL;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl1 = "http://localhost:%s".formatted(mockWebServer.getPort());
        String baseUrl2 = "http://localhost:%s".formatted(mockWebServer.getPort());
        userService = Mockito.mock(UserService.class);
        greenCityRemoteClient = new GreenCityRemoteClient(WebClient.builder().baseUrl(baseUrl1).build(),
            WebClient.builder().baseUrl(baseUrl2).build(), userService);
    }

    @Test
    @SneakyThrows
    void uploadAllFilesTest() {
        List<String> expectedUrls = List.of("url1", "url2");
        String expectedJson = toJson(expectedUrls);
        String expectedRequestPath = "/files";
        String expectedRequestMethod = HttpMethod.POST.name();

        MultipartFile file1 = createMockMultipartFile("file1.txt", "content1");
        MultipartFile file2 = createMockMultipartFile("file2.txt", "content2");
        List<MultipartFile> files = Arrays.asList(file1, file2);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<String> actualResult = greenCityRemoteClient.uploadAllFiles(files);

        assertEquals(expectedUrls, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
        assertTrue(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @Test
    @SneakyThrows
    void uploadFileTest() {
        String expectedUrl = "uploaded-file-url";
        String expectedRequestPath = "/files/single";
        String expectedRequestMethod = HttpMethod.POST.name();

        MultipartFile file = createMockMultipartFile("file.txt", "content");

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedUrl)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String actualResult = greenCityRemoteClient.uploadFile(file);

        assertEquals(expectedUrl, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
        assertTrue(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @Test
    @SneakyThrows
    void deleteAllFilesTest() {
        List<String> pathsToDelete = Arrays.asList("path1", "path2");
        String expectedRequestPath = "/files";
        String expectedRequestMethod = HttpMethod.DELETE.name();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        greenCityRemoteClient.deleteAllFiles(pathsToDelete);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());

        String requestBody = recordedRequest.getBody().readUtf8();
        List<String> actualPaths = fromJson(requestBody, new TypeReference<>() {
        });
        assertEquals(pathsToDelete, actualPaths);
    }

    @Test
    @SneakyThrows
    void findAllAchievementsTest() {
        List<AchievementVO> expectedAchievements = Arrays.asList(
            ModelUtils.getAchievementVO(),
            ModelUtils.getAchievementVO());
        String expectedJson = toJson(expectedAchievements);
        String expectedRequestPath = "/achievements/all";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<AchievementVO> actualResult = greenCityRemoteClient.findAllAchievements();

        assertEquals(expectedAchievements, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllUserAchievementsByUserIdTest() {
        List<UserAchievementVO> expectedUserAchievements = Arrays.asList(
            ModelUtils.getUserAchievementVO(),
            ModelUtils.getUserAchievementVO());
        String expectedJson = toJson(expectedUserAchievements);
        String expectedRequestPath = "/achievements/user-achievements?email=" + userEmail;
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserAchievementVO> actualResult = greenCityRemoteClient.findAllUserAchievementsByUserId(userId);

        assertEquals(expectedUserAchievements, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllUsersCitiesTest() {
        UserCityDto expectedUserCityDto = ModelUtils.getUserCityDto();
        String expectedJson = toJson(expectedUserCityDto);
        String expectedRequestPath = "/users/user/cities?email=" + userEmail;
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        UserCityDto actualResult = greenCityRemoteClient.findAllUsersCities(userId);

        assertEquals(expectedUserCityDto, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void setLocationForUserTest() {
        UserProfileDtoRequest userProfileDtoRequest = ModelUtils.getUserProfileDtoRequest();
        String expectedRequestPath = "/users/user/location?email=" + userEmail;
        String expectedRequestMethod = HttpMethod.PATCH.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        greenCityRemoteClient.setLocationForUser(userId, userProfileDtoRequest);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());

        String requestBody = recordedRequest.getBody().readUtf8();
        UserProfileDtoRequest actualRequest = fromJson(requestBody, UserProfileDtoRequest.class);
        assertEquals(userProfileDtoRequest, actualRequest);
    }

    @Test
    @SneakyThrows
    void getAllUserFriendsIdsTest() {
        List<Long> expectedFriendsIds = Arrays.asList(1L, 2L, 3L);
        String expectedJson = toJson(expectedFriendsIds);
        String expectedRequestPath = "/users/user/all-friends?email=" + userEmail;
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<Long> actualResult = greenCityRemoteClient.getAllUserFriendsIds(userId);

        assertEquals(expectedFriendsIds, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getAllUserFriendsIdsWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> friendIds = List.of(1L, 2L, 3L);
        PageableAdvancedDto<Long> usersIdsPage = new PageableAdvancedDto<>(
            friendIds,
            friendIds.size(),
            0,
            1,
            0,
            false,
            false,
            true,
            true);
        String expectedJson = toJson(usersIdsPage);
        String expectedRequestPath = "/users/user/friends?email=" + userEmail + "&page=0&size=10";
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        PageableAdvancedDto<Long> actualResult = greenCityRemoteClient.getAllUserFriendsIds(userId, pageable);

        assertEquals(friendIds, actualResult.getPage());
        assertEquals(friendIds.size(), actualResult.getTotalElements());
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(pageQueryParam));
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(sizeQueryParam));
        assertEquals("0", recordedRequest.getRequestUrl().queryParameter(pageQueryParam));
        assertEquals("10", recordedRequest.getRequestUrl().queryParameter(sizeQueryParam));

    }

    @Test
    @SneakyThrows
    void getSixFriendsIdsWithTheHighestRatingTest() {
        List<Long> expectedTopFriendsIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        String expectedJson = toJson(expectedTopFriendsIds);
        String expectedRequestPath = "/users/user/top-friends?email=" + userEmail;
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<Long> actualResult = greenCityRemoteClient.getSixFriendsIdsWithTheHighestRating(userId);

        assertEquals(expectedTopFriendsIds, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void updateUserRatingTest() {
        UserAddRatingExternalDto userAddRatingDto = new UserAddRatingExternalDto(userEmail, 2.);
        String expectedRequestPath = "/users/user-rating";
        String expectedRequestMethod = HttpMethod.PATCH.name();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        greenCityRemoteClient.updateUserRating(userAddRatingDto);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());

        String requestBody = recordedRequest.getBody().readUtf8();
        UserAddRatingExternalDto actualRequest = fromJson(requestBody, UserAddRatingExternalDto.class);
        assertEquals(userAddRatingDto, actualRequest);
    }

    @Test
    @SneakyThrows
    void createUserTest_ReturnsTrue() {
        CreateGreenCityUserDto createUserDto = ModelUtils.getCreateGreenCityUserDto();
        String expectedRequestPath = "/users/create";
        String expectedRequestMethod = HttpMethod.POST.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody("true")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        boolean actualResult = greenCityRemoteClient.createUser(createUserDto);

        assertTrue(actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());

        String requestBody = recordedRequest.getBody().readUtf8();
        CreateGreenCityUserDto actualRequest = fromJson(requestBody, CreateGreenCityUserDto.class);
        assertEquals(createUserDto, actualRequest);
    }

    @Test
    @SneakyThrows
    void createUserTest_ReturnsFalse() {
        CreateGreenCityUserDto createUserDto = ModelUtils.getCreateGreenCityUserDto();
        String expectedRequestPath = "/users/create";
        String expectedRequestMethod = HttpMethod.POST.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody("false")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        boolean actualResult = greenCityRemoteClient.createUser(createUserDto);

        assertFalse(actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void updateUserPicturePathTest() {
        String profilePicturePath = "/path/to/picture.jpg";
        String expectedRequestPath =
            "/users/user/picturePath?email=" + userEmail + "&profilePicturePath=" + profilePicturePath;
        String expectedRequestMethod = HttpMethod.PUT.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        greenCityRemoteClient.updateUserPicturePath(userId, profilePicturePath);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(userEmailQueryParam));
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(profilePicturePathQueryParam));
        assertEquals(userEmail, recordedRequest.getRequestUrl().queryParameter(userEmailQueryParam));
        assertEquals(profilePicturePath, recordedRequest.getRequestUrl().queryParameter(profilePicturePathQueryParam));
    }

    @Test
    @SneakyThrows
    void updateUserNameTest() {
        String userName = "newUserName";
        String expectedRequestPath = "/users/user/name?email=" + userEmail + "&userName=" + userName;
        String expectedRequestMethod = HttpMethod.PATCH.name();

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        greenCityRemoteClient.updateUserName(userId, userName);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(userNameQueryParam));
        assertEquals(userName, recordedRequest.getRequestUrl().queryParameter(userNameQueryParam));
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfilesByUserIdsTest() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        List<GreenCityUserProfileDtoResponse> expectedProfiles = Arrays.asList(
            ModelUtils.getGreenCityUserProfileDtoResponse(),
            ModelUtils.getGreenCityUserProfileDtoResponse(),
            ModelUtils.getGreenCityUserProfileDtoResponse());
        String expectedJson = toJson(expectedProfiles);
        String expectedRequestPath = "/users/profiles/external?emails="
            + "test@email1&emails=test@email2&emails=test@email3";
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findAllEmailsByIdIn(userIds))
            .thenReturn(List.of("test@email1", "test@email2", "test@email3"));

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<GreenCityUserProfileDtoResponse> actualResult =
            greenCityRemoteClient.findGreenCityUserProfilesByUserIds(userIds);

        assertEquals(expectedProfiles, actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(userEmailsQueryParam));
    }

    @Test
    @SneakyThrows
    void findGreenCityUserProfileByUserIdTest() {
        List<GreenCityUserProfileDtoResponse> profiles = List.of(
            ModelUtils.getGreenCityUserProfileDtoResponse());
        String expectedJson = toJson(profiles);
        String expectedRequestPath = "/users/profiles/external?emails=test@email";
        String expectedRequestMethod = HttpMethod.GET.name();

        when(userService.findAllEmailsByIdIn(List.of(userId))).thenReturn(List.of("test@email"));

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        GreenCityUserProfileDtoResponse actualResult = greenCityRemoteClient.findGreenCityUserProfileByUserId(userId);

        assertEquals(profiles.getFirst(), actualResult);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(userEmailsQueryParam));
    }

    private MultipartFile createMockMultipartFile(String name, String content) {
        return new MockMultipartFile(name, name, "text/plain", content.getBytes());
    }

    @SneakyThrows
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    private <T> T fromJson(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    private <T> T fromJson(String json, TypeReference<T> typeReference) {
        return objectMapper.readValue(json, typeReference);
    }

    @Test
    @SneakyThrows
    void createUbsProfile() {
        UbsProfileCreationDto dto = ModelUtils.getUbsProfileCreationDto();
        Long expectedId = 123L;
        String expectedPath = "/ubs/userProfile/user/create";
        String expectedMethod = HttpMethod.POST.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedId.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualId = greenCityRemoteClient.createUbsProfile(dto);

        assertEquals(expectedId, actualId);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());

        String requestBody = recordedRequest.getBody().readUtf8();
        UbsProfileCreationDto actualRequest = fromJson(requestBody, UbsProfileCreationDto.class);
        assertEquals(dto, actualRequest);
    }

    @Test
    @SneakyThrows
    void getAllAvailableCustomToDoListItems() {
        Long habitId = 2L;
        String expectedPath = "/custom/to-do-list-items?email=" + userEmail + "&habitId=" + habitId;
        String expectedMethod = HttpMethod.GET.name();

        List<CustomToDoListItemResponseDto> expectedResponse = List.of(
            new CustomToDoListItemResponseDto(1L, "Test 1"),
            new CustomToDoListItemResponseDto(2L, "Test 2"));

        String responseBody = toJson(expectedResponse);

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(responseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<CustomToDoListItemResponseDto> actualResponse =
            greenCityRemoteClient.getAllAvailableCustomToDoListItems(userId, habitId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAmountOfPublishedNews() {
        String expectedPath = "/eco-news/count/external?authorEmail=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        Long expectedCount = 5L;

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualCount = greenCityRemoteClient.findAmountOfPublishedNews(userId);
        assertEquals(expectedCount, actualCount);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAmountOfAcquiredHabits() {
        String expectedPath = "/habit/statistic/acquired/count/external?email=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        Long expectedCount = 5L;

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualCount = greenCityRemoteClient.findAmountOfAcquiredHabits(userId);
        assertEquals(expectedCount, actualCount);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAmountOfHabitsInProgress() {
        String expectedPath = "/habit/statistic/in-progress/count/external?email=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        Long expectedCount = 5L;

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualCount = greenCityRemoteClient.findAmountOfHabitsInProgress(userId);
        assertEquals(expectedCount, actualCount);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAmountOfEventsOrganizedByUserTest() {
        String expectedPath = "/events/organizers/count/external?email=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        Long expectedCount = 5L;

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualCount = greenCityRemoteClient.findAmountOfEventsOrganizedByUser(userId);
        assertEquals(expectedCount, actualCount);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAmountOfEventsAttendedByUserTest() {
        String expectedPath = "/events/attenders/count/external?email=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        Long expectedCount = 5L;

        when(userService.findById(userId)).thenReturn(user);

        mockWebServer.enqueue(new MockResponse()
            .setBody(expectedCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualCount = greenCityRemoteClient.findAmountOfEventsAttendedByUser(userId);
        assertEquals(expectedCount, actualCount);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getGreenCityUserStatusTest() {
        String expectedPath = "/users/status?email=" + userEmail;
        String expectedMethod = HttpMethod.GET.name();
        ServiceUserStatus expectedStatus = ServiceUserStatus.ACTIVATED;

        mockWebServer.enqueue(new MockResponse()
            .setBody(toJson(expectedStatus.name()))
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        ServiceUserStatus actualStatus = greenCityRemoteClient.getGreenCityUserStatus(userEmail);
        assertEquals(expectedStatus, actualStatus);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getUbsUserStatus() {
        String userUuid = UUID.randomUUID().toString();
        String expectedPath = "/ubs/userProfile/user/status?uuid=" + userUuid;
        String expectedMethod = HttpMethod.GET.name();
        ServiceUserStatus expectedStatus = ServiceUserStatus.ACTIVATED;

        mockWebServer.enqueue(new MockResponse()
            .setBody(toJson(expectedStatus.name()))
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        ServiceUserStatus actualStatus = greenCityRemoteClient.getUbsUserStatus(userUuid);
        assertEquals(expectedStatus, actualStatus);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedMethod, recordedRequest.getMethod());
        assertEquals(expectedPath, recordedRequest.getPath());
    }
}
