package greencity.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.*;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacebookSecurityServiceImplTest {

    @InjectMocks
    private FacebookSecurityServiceImpl facebookSecurityService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtTool jwtTool;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestClient restClient;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(facebookSecurityService, "address", "http://localhost:8060");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppId", "12345");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppSecret", "6789");
        ReflectionTestUtils.setField(facebookSecurityService, "userInfoUrl", "https://graph.facebook.com/me");
    }

    @Test
    void createNewUser_ShouldReturnUserWithDefaultValues() {
        String email = "test@example.com";
        String userName = "Test User";
        String profilePicture = "profile.jpg";
        String language = "1";
        when(modelMapper.map(language, Long.class)).thenReturn(1L);

        User user = facebookSecurityService.createNewUser(email, userName, profilePicture, language);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(userName, user.getName());
        assertEquals(Role.ROLE_USER, user.getRole());
        assertEquals(UserStatus.ACTIVATED, user.getUserStatus());
        assertEquals(EmailNotification.DISABLED, user.getEmailNotification());
        assertEquals(profilePicture, user.getProfilePicturePath());
        assertEquals(ProfilePrivacyPolicy.PUBLIC, user.getShowLocation());
        assertEquals(1L, user.getLanguage().getId());
    }

    @Test
    void createNewUser_ShouldThrowException_WhenLanguageIsNull() {
        String email = "test@example.com";
        String userName = "Test User";
        String profilePicture = "profile.jpg";
        String language = null;

        when(modelMapper.map(language, Long.class)).thenThrow(new IllegalArgumentException("Language cannot be null"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.createNewUser(email, userName, profilePicture, language));
        assertNotNull(exception);
    }

    @Test
    void authenticate_ShouldThrowException_WhenTokenOrLanguageIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.authenticate(null, "en"));
        assertEquals(ErrorMessage.FB_TOKEN_OR_LANGUAGE_MISSING, exception.getMessage());
    }

    @Test
    void authenticate_ShouldReturnSuccessSignInDto_WhenValidToken() {
        String fbToken = "token";
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("test@example.com");
        userInfo.setName("Test User");
        userInfo.setPicture("profile.jpg");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserInfo.class)).thenReturn(Mono.just(userInfo));

        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setEmail("test@example.com");
        userVO.setName("Test User");
        userVO.setUserStatus(UserStatus.ACTIVATED);
        userVO.setRole(Role.ROLE_USER);

        when(userService.findByEmail("test@example.com")).thenReturn(userVO);
        when(jwtTool.createAccessToken(userVO.getEmail(), userVO.getRole())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(userVO)).thenReturn("refreshToken");

        SuccessSignInDto result = facebookSecurityService.authenticate(fbToken, "en");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void authenticate_ShouldThrowException_WhenFacebookReturnsInvalidData() {
        String fbToken = "fakeToken";
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserInfo.class))
            .thenReturn(Mono.error(new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.authenticate(fbToken, "en"));
        assertTrue(exception.getMessage().contains(ErrorMessage.BAD_FACEBOOK_TOKEN));
    }

    @Test
    void processAuthentication_ShouldReturnExistingUser_WhenUserExists() {
        String email = "test@example.com";
        UserVO userVO = new UserVO();
        userVO.setEmail(email);
        userVO.setUserStatus(UserStatus.ACTIVATED);
        userVO.setRole(Role.ROLE_USER);

        when(userService.findByEmail(email)).thenReturn(userVO);
        when(jwtTool.createAccessToken(userVO.getEmail(), userVO.getRole())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(userVO)).thenReturn("refreshToken");

        SuccessSignInDto result = facebookSecurityService.processAuthentication(email, "Test User", "profile.jpg", "1");

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void processAuthentication_ShouldThrowException_WhenUserIsDeactivated() {
        String email = "deactivated@example.com";
        UserVO userVO = new UserVO();
        userVO.setEmail(email);
        userVO.setUserStatus(UserStatus.DEACTIVATED);
        when(userService.findByEmail(email)).thenReturn(userVO);
        assertThrows(UserDeactivatedException.class,
            () -> facebookSecurityService.processAuthentication(email, "Test User", "profile.jpg", "1"));
    }

    @Test
    void saveNewUser_ShouldSaveUserAndGenerateUUID() {
        User newUser = new User();
        newUser.setEmail("test@example.com");
        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setId(1L);
        savedUser.setUuid(UUID.randomUUID().toString());

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            user.setUuid(UUID.randomUUID().toString());
            return user;
        });

        User result = facebookSecurityService.saveNewUser(newUser);
        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertEquals(1L, result.getId());
    }

    @Test
    void getSuccessSignInDto_ShouldReturnDtoWithTokens() {
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setEmail("test@example.com");
        userVO.setName("Test User");
        userVO.setRole(Role.ROLE_USER);

        when(jwtTool.createAccessToken(userVO.getEmail(), userVO.getRole())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(userVO)).thenReturn("refreshToken");

        SuccessSignInDto result = facebookSecurityService.getSuccessSignInDto(userVO);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void generateFacebookAuthorizeURLTest() {
        String expected = "https://www.facebook.com/v22.0/dialog/oauth" +
            "?client_id=12345" +
            "&redirect_uri=http://localhost:8060/facebookSecurity/facebook" +
            "&scope=email";

        String actual = facebookSecurityService.generateFacebookAuthorizeURL();
        assertEquals(expected, actual);
    }

    @Test
    void generateFacebookAccessToken_ShouldThrowException_WhenInvalidCode() {
        String code = "invalid_code";
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
            .thenReturn(Mono.error(new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.generateFacebookAccessToken(code));
        assertTrue(exception.getMessage().contains(ErrorMessage.BAD_FACEBOOK_TOKEN));
    }

    @Test
    void generateFacebookAccessToken_ShouldThrowException_WhenAccessTokenIsNull() throws Exception {
        String code = "valid_code";
        String accessTokenResponse = "{}";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(accessTokenResponse));

        JsonNode accessTokenNode = mock(JsonNode.class);
        when(objectMapper.readTree(accessTokenResponse)).thenReturn(accessTokenNode);
        when(accessTokenNode.get("access_token")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.generateFacebookAccessToken(code));
        assertTrue(exception.getMessage().contains(ErrorMessage.BAD_FACEBOOK_TOKEN));
    }

    @Test
    void handleNewUser_ShouldCreateAndSaveUser() {
        String email = "newuser@example.com";
        String userName = "New User";
        String profilePicture = "profile.jpg";
        String language = "1";

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setName(userName);

        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setName(userName);
        userVO.getRole();

        UbsProfileCreationDto profileDto = new UbsProfileCreationDto();

        when(modelMapper.map(language, Long.class)).thenReturn(1L);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(User.class), eq(UbsProfileCreationDto.class))).thenReturn(profileDto);
        when(jwtTool.createAccessToken(userVO.getEmail(), userVO.getRole())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(userVO)).thenReturn("refreshToken");

        SuccessSignInDto result = facebookSecurityService.handleNewUser(email, userName, profilePicture, language);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(userName, result.getName());
    }

    @Test
    void authenticate_ShouldThrowException_WhenEmailIsNull() {
        String fbToken = "fakeToken";
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(null);
        userInfo.setName("name");
        userInfo.setPicture("picture.png");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserInfo.class)).thenReturn(Mono.just(userInfo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.authenticate(fbToken, "en"));
        assertTrue(exception.getMessage().contains(ErrorMessage.BAD_FACEBOOK_TOKEN));
    }

    @Test
    void handleNewUser_ShouldThrowException_WhenRestClientFails() {
        String email = "newuser@example.com";
        String userName = "New User";
        String profilePicture = "profile.jpg";
        String language = "1";

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(email);

        when(modelMapper.map(language, Long.class)).thenReturn(1L);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(any(User.class), eq(UbsProfileCreationDto.class)))
            .thenReturn(new UbsProfileCreationDto());
        when(restClient.createUbsProfile(any())).thenThrow(new RestClientException("Failed to create UBS profile"));

        RestClientException exception = assertThrows(RestClientException.class,
            () -> facebookSecurityService.handleNewUser(email, userName, profilePicture, language));
        assertTrue(exception.getMessage().contains(ErrorMessage.TRANSACTION_FAILED));
    }

    @Test
    void generateFacebookAccessToken_ShouldThrowException_WhenEmailIsNull() throws Exception {
        String code = "valid_code";
        String accessTokenResponse = "{\"access_token\": \"valid_token\"}";
        String userInfoResponse = "{\"name\": \"Test User\"}";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
            .thenReturn(Mono.just(accessTokenResponse))
            .thenReturn(Mono.just(userInfoResponse));

        JsonNode accessTokenNode = mock(JsonNode.class);
        when(objectMapper.readTree(accessTokenResponse)).thenReturn(accessTokenNode);
        when(accessTokenNode.get("access_token")).thenReturn(accessTokenNode);
        when(accessTokenNode.asText()).thenReturn("valid_token");

        JsonNode userInfoNode = mock(JsonNode.class);
        when(objectMapper.readTree(userInfoResponse)).thenReturn(userInfoNode);
        when(userInfoNode.has("email")).thenReturn(false);
        when(userInfoNode.has("name")).thenReturn(true);
        when(userInfoNode.get("name")).thenReturn(userInfoNode);
        when(userInfoNode.asText()).thenReturn("Test User");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.generateFacebookAccessToken(code));
        assertTrue(exception.getMessage().contains(ErrorMessage.BAD_FACEBOOK_TOKEN));
    }

    @Test
    void createNewUser_ShouldHandleNullLanguage() {
        String email = "test@example.com";
        String userName = "Test User";
        String profilePicture = "profile.jpg";
        String language = null;

        when(modelMapper.map(language, Long.class)).thenThrow(new IllegalArgumentException("Language cannot be null"));

        assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.createNewUser(email, userName, profilePicture, language));
    }

    @Test
    void createNewUser_ShouldReturnUserWithGivenEmailAndUserName() {
        String email = "test@example.com";
        String userName = "testUser";
        when(jwtTool.generateTokenKey()).thenReturn("fakeTokenKey");

        User user = facebookSecurityService.createNewUser(email, userName);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(userName, user.getName());
        assertEquals("fakeTokenKey", user.getRefreshTokenKey());
    }

    @Test
    void createNewUser_WithPreferences_ShouldSetNotificationPreferences() {
        String email = "test@example.com";
        String userName = "Test User";
        String profilePicture = "profile.jpg";
        String language = "1";
        when(modelMapper.map(language, Long.class)).thenReturn(1L);

        User user = facebookSecurityService.createNewUser(email, userName, profilePicture, language);

        assertNotNull(user.getNotificationPreferences());
        assertEquals(EmailPreference.values().length, user.getNotificationPreferences().size());
        user.getNotificationPreferences().forEach(pref -> {
            assertEquals(EmailPreferencePeriodicity.TWICE_A_DAY, pref.getPeriodicity());
            assertNotNull(pref.getEmailPreference());
        });
    }

    @Test
    void createNewUser_ShouldSetNotificationPreferencesCorrectly() {
        String email = "test@example.com";
        String userName = "Test User";
        String profilePicture = "profile.jpg";
        String language = "1";

        when(modelMapper.map(language, Long.class)).thenReturn(1L);

        User user = facebookSecurityService.createNewUser(email, userName, profilePicture, language);

        assertNotNull(user.getNotificationPreferences());
        assertEquals(EmailPreference.values().length, user.getNotificationPreferences().size());
        user.getNotificationPreferences().forEach(pref -> {
            assertEquals(EmailPreferencePeriodicity.TWICE_A_DAY, pref.getPeriodicity());
            assertNotNull(pref.getEmailPreference());
            assertEquals(user, pref.getUser());
        });
    }
}