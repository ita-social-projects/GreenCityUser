package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.RestClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOReducedDto;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.IdTokenExpiredException;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.AchievementService;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static greencity.ModelUtils.getUserInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoogleSecurityServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    @Mock
    private JwtTool jwtTool;
    @Mock
    private GoogleIdToken googleIdToken;
    @Mock
    private ModelMapper modelMapper;
    @Spy
    private GoogleIdToken.Payload payload;
    @Mock
    private AchievementService achievementService;
    @Mock
    private RestClient restClient;
    @Mock
    private PlatformTransactionManager platformTransactionManager;
    @Mock
    private HttpClient googleAccessTokenVerifier;
    @Mock
    private HttpResponse httpResponse;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private GoogleSecurityServiceImpl googleSecurityService;

    @Test
    void authenticateByIdTokenTest() throws GeneralSecurityException, IOException {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();

        when(googleIdTokenVerifier.verify("token")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmailReduced("test@mail.com")).thenReturn(userVO);

        SuccessSignInDto result = googleSecurityService.authenticate("token", "ua");
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getId(), result.getUserId());

        verify(googleIdTokenVerifier).verify("token");
        verify(googleIdToken, times(3)).getPayload();
        verify(payload).getEmail();
        verify(userService).findByEmailReduced("test@mail.com");
    }

    @Test
    void authenticateByAccessTokenTest() throws GeneralSecurityException, IOException {
        UserInfo userInfo = getUserInfo();
        UserVO userVO = ModelUtils.getUserVO();

        String expectedJsonResponse = new ObjectMapper().writeValueAsString(userInfo);
        HttpEntity httpEntity = new StringEntity(expectedJsonResponse);

        when(googleIdTokenVerifier.verify("token")).thenThrow(IllegalArgumentException.class);
        when(googleAccessTokenVerifier.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(objectMapper.readValue(expectedJsonResponse, UserInfo.class)).thenReturn(userInfo);
        when(userService.findByEmailReduced(userInfo.getEmail())).thenReturn(userVO);

        SuccessSignInDto result = googleSecurityService.authenticate("token", "ua");

        assertEquals(userVO.getName(), result.getName());
        assertEquals(userVO.getId(), result.getUserId());

        verify(googleIdTokenVerifier).verify("token");
        verify(googleAccessTokenVerifier).execute(any(HttpGet.class));
        verify(httpResponse).getEntity();
        verify(objectMapper).readValue(expectedJsonResponse, UserInfo.class);
        verify(userService).findByEmailReduced(userInfo.getEmail());
    }

    @Test
    void authenticateNullUserTest() throws GeneralSecurityException, IOException {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();

        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());

        userVO.setId(null);
        userVO.setName(null);
        user.setId(null);
        user.setName(null);

        when(googleIdTokenVerifier.verify("token")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("taras@mail.com");

        when(userService.findByEmailReduced("taras@mail.com")).thenReturn(null);

        when(modelMapper.map(any(), eq(UserVOReducedDto.class))).thenReturn(userVO);
        when(userRepo.save(any())).thenReturn(user);
        when(achievementService.findAll()).thenReturn(achievementVOList);
        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(UbsProfileCreationDto.builder().build());
        when(restClient.createUbsProfile(any(UbsProfileCreationDto.class))).thenReturn(1L);

        SuccessSignInDto result = googleSecurityService.authenticate("token", "ua");

        assertNull(result.getUserId());
        assertNull(result.getName());

        verify(googleIdTokenVerifier).verify("token");
        verify(googleIdToken, times(3)).getPayload();
        verify(payload).getEmail();

        verify(userService).findByEmailReduced("taras@mail.com");

        verify(modelMapper).map(any(), eq(UserVOReducedDto.class));
        verify(userRepo).save(argThat(savedUser -> {
            assertEquals(Role.ROLE_USER, savedUser.getRole(), "Role should be USER.");
            assertEquals(UserStatus.ACTIVATED, savedUser.getUserStatus(), "User status should be ACTIVATED.");
            assertNotNull(savedUser.getDateOfRegistration(), "Date of registration should be set.");
            assertNotNull(savedUser.getLastActivityTime(), "Last activity time should be set.");
            assertEquals(EmailNotification.DISABLED, savedUser.getEmailNotification(),
                "Email notification should be DISABLED.");
            // assertEquals(DEFAULT_RATING, savedUser.getRating());
            assertEquals(ProfilePrivacyPolicy.PUBLIC, savedUser.getShowLocation());
            assertEquals(ProfilePrivacyPolicy.PUBLIC, savedUser.getShowEcoPlace());
            assertEquals(ProfilePrivacyPolicy.PUBLIC, savedUser.getShowToDoList());
            assertNotNull(savedUser.getNotificationPreferences(), "Notification preferences should be initialized.");
            assertFalse(savedUser.getNotificationPreferences().isEmpty(),
                "Notification preferences should not be empty.");
            return true;
        }));
    }

    @Test
    void authenticationThrowsUserDeactivatedExceptionTest() throws GeneralSecurityException, IOException {
        UserVO userVO = UserVO.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME)
            .role(Role.ROLE_USER).userStatus(UserStatus.DEACTIVATED)
            .lastActivityTime(LocalDateTime.now()).dateOfRegistration(LocalDateTime.now())
            .build();

        when(googleIdTokenVerifier.verify("token")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmailReduced("test@mail.com")).thenReturn(userVO);

        assertThrows(UserDeactivatedException.class,
            () -> googleSecurityService.authenticate("token", "ua"));

        verify(googleIdTokenVerifier).verify("token");
        verify(googleIdToken, times(3)).getPayload();
        verify(payload).getEmail();
        verify(userService).findByEmailReduced("test@mail.com");
    }

    @Test
    void authenticateThrowsIllegalArgumentExceptionWhenAccessTokenIsInvalidOrExpiredTest()
        throws GeneralSecurityException, IOException {
        UserInfo userInfo = new UserInfo();

        String expectedJsonResponse = new ObjectMapper().writeValueAsString(userInfo);
        HttpEntity httpEntity = new StringEntity(expectedJsonResponse);

        when(googleIdTokenVerifier.verify("token")).thenThrow(IllegalArgumentException.class);
        when(googleAccessTokenVerifier.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(objectMapper.readValue(expectedJsonResponse, UserInfo.class)).thenReturn(userInfo);

        assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("token", "ua"));

        verify(googleIdTokenVerifier).verify("token");
        verify(googleAccessTokenVerifier).execute(any(HttpGet.class));
        verify(httpResponse).getEntity();
        verify(objectMapper).readValue(expectedJsonResponse, UserInfo.class);
    }

    @Test
    void authenticateThrowsIdTokenExpiredExceptionTest() throws IOException, GeneralSecurityException {
        when(googleIdTokenVerifier.verify("token")).thenReturn(null);
        assertThrows(IdTokenExpiredException.class,
            () -> googleSecurityService.authenticate("token", "ua"));
        verify(googleIdTokenVerifier).verify("token");
    }

    @Test
    void authenticateThrowsIllegalArgumentExceptionWhenIdTokenIsInvalidTest()
        throws IOException, GeneralSecurityException {
        when(googleIdTokenVerifier.verify("token")).thenThrow(IOException.class);
        assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("token", "ua"));
        verify(googleIdTokenVerifier).verify("token");
    }

    @Test
    void authenticateThrowsIllegalArgumentExceptionWhenAccessTokenIsInvalidTest()
        throws IOException, GeneralSecurityException {
        when(googleIdTokenVerifier.verify("token")).thenThrow(IllegalArgumentException.class);
        when(googleAccessTokenVerifier.execute(any(HttpGet.class))).thenThrow(IOException.class);
        assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("token", "ua"));
        verify(googleIdTokenVerifier).verify("token");
    }
}