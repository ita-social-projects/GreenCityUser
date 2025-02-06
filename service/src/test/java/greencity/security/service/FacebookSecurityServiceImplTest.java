package greencity.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class FacebookSecurityServiceImplTest {

    @InjectMocks
    private FacebookSecurityServiceImpl facebookSecurityService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private User user;

    @Mock
    private JwtTool jwtTool;

    @Mock
    private UserService userService;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(facebookSecurityService, "address", "http://localhost:8060");
        ReflectionTestUtils.setField(facebookSecurityService, "jwtTool", jwtTool);
        ReflectionTestUtils.setField(facebookSecurityService, "restClient", restClient);
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
    void authenticate_ShouldThrowException_WhenTokenOrLanguageIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> facebookSecurityService.authenticate(null, "en"));
        assertEquals(ErrorMessage.FB_TOKEN_OR_LANGUAGE_MISSING, exception.getMessage());
    }

    @Test
    void authenticate_ShouldThrowException_WhenFacebookReturnsInvalidData() throws IOException {
        String fbToken = "invalid_token";
        HttpGet mockRequest = new HttpGet("http://fake-url.com");
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(400);
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
        when(userService.findByEmail(email)).thenReturn(userVO);
        when(jwtTool.createAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(any())).thenReturn("refreshToken");
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
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
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
        when(jwtTool.createAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtTool.createRefreshToken(any())).thenReturn("refreshToken");
        SuccessSignInDto result = facebookSecurityService.getSuccessSignInDto(userVO);
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void generateFacebookAuthorizeURLTest() {
        ReflectionTestUtils.setField(facebookSecurityService, "address", "http://localhost:8080");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppId", "12345");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppSecret", "6789");

        String expected = """
            https://www.facebook.com/v2.5/dialog/oauth?client_id=12345&response_type=code&redirect\
            _uri=http%3A%2F%2Flocalhost%3A8080%2FfacebookSecurity%2Ffacebook&scope=email\
            """;
        String actual = facebookSecurityService.generateFacebookAuthorizeURL();
        assertEquals(expected, actual);
    }

    @Test
    void getUserInfoFromFacebook_ShouldThrowIOException_WhenResponseStatusIsNot200() throws IOException {
        String accessToken = "invalid_token";

        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(400);

        IOException exception =
            assertThrows(IOException.class, () -> facebookSecurityService.getUserInfoFromFacebook(accessToken));
        assertTrue(exception.getMessage().contains("Facebook API returned status"));
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

        UbsProfileCreationDto profileDto = new UbsProfileCreationDto();

        doReturn(1L).when(modelMapper).map(language, Long.class);
        doReturn(savedUser).when(userRepo).save(any(User.class));
        doReturn(userVO).when(modelMapper).map(any(User.class), eq(UserVO.class));
        doReturn(profileDto).when(modelMapper).map(any(User.class), eq(UbsProfileCreationDto.class));
        doReturn("accessToken").when(jwtTool).createAccessToken(any(), any());
        doReturn("refreshToken").when(jwtTool).createRefreshToken(any());

        SuccessSignInDto result = facebookSecurityService.handleNewUser(email, userName, profilePicture, language);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(userName, result.getName());
    }

    @Test
    void getUserInfoFromFacebook_ShouldThrowException_WhenFacebookReturnsError() throws IOException {
        String accessToken = "invalid_token";
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(400);

        IOException exception =
            assertThrows(IOException.class, () -> facebookSecurityService.getUserInfoFromFacebook(accessToken));
        assertTrue(exception.getMessage().contains("Facebook API returned status"));
    }
}