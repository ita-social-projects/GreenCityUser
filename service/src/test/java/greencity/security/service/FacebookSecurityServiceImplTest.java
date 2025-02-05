package greencity.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
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
    private JwtTool jwtTool;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(facebookSecurityService, "address", "http://localhost:8080");
        ReflectionTestUtils.setField(facebookSecurityService, "jwtTool", jwtTool);
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
}
