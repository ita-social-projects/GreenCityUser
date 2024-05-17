package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.entity.Achievement;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.Role;
import greencity.enums.UserStatus;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private HttpClient httpClient;
    @Mock
    private HttpResponse httpResponse;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private GoogleSecurityServiceImpl googleSecurityService;

    @Test
    void authenticateWithIdTokenTest() throws GeneralSecurityException, IOException {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();

        when(googleIdTokenVerifier.verify("idToken")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmail("test@mail.com")).thenReturn(userVO);

        SuccessSignInDto result = googleSecurityService.authenticate("idToken", "ua");
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getId(), result.getUserId());

        verify(googleIdTokenVerifier).verify("idToken");
        verify(googleIdToken, times(2)).getPayload();
        verify(payload).getEmail();
        verify(userService).findByEmail("test@mail.com");
    }

    @Test
    void authenticateWithAccessTokenTest() throws GeneralSecurityException, IOException {
        UserInfo userInfo = getUserInfo();
        UserVO userVO = ModelUtils.getUserVO();

        String expectedJsonResponse = new ObjectMapper().writeValueAsString(userInfo);
        HttpEntity httpEntity = new StringEntity(expectedJsonResponse);

        when(googleIdTokenVerifier.verify("accessToken")).thenThrow(GeneralSecurityException.class);
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(objectMapper.readValue(expectedJsonResponse, UserInfo.class)).thenReturn(userInfo);
        when(userService.findByEmail(userInfo.getEmail())).thenReturn(userVO);

        SuccessSignInDto result = googleSecurityService.authenticate("accessToken", "ua");

        assertEquals(userVO.getName(), result.getName());
        assertEquals(userVO.getId(), result.getUserId());

        verify(googleIdTokenVerifier).verify("accessToken");
        verify(httpClient).execute(any(HttpGet.class));
        verify(httpResponse).getEntity();
        verify(objectMapper).readValue(expectedJsonResponse, UserInfo.class);
        verify(userService).findByEmail(userInfo.getEmail());
    }

    @Test
    void authenticateNullUserTest() throws GeneralSecurityException, IOException {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();

        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());

        userVO.setId(null);
        userVO.setName(null);
        user.setId(null);
        user.setName(null);
        user.setUserAchievements(userAchievementList);

        when(googleIdTokenVerifier.verify("idToken")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("taras@mail.com");

        when(userService.findByEmail("taras@mail.com")).thenReturn(null);

        when(modelMapper.map(any(), eq(UserVO.class))).thenReturn(userVO);
        when(userRepo.save(any())).thenReturn(user);
        when(achievementService.findAll()).thenReturn(achievementVOList);
        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(UbsProfileCreationDto.builder().build());
        when(restClient.createUbsProfile(any(UbsProfileCreationDto.class))).thenReturn(1L);

        SuccessSignInDto result = googleSecurityService.authenticate("idToken", "ua");

        assertNull(result.getUserId());
        assertNull(result.getName());

        verify(googleIdTokenVerifier).verify("idToken");
        verify(googleIdToken, times(3)).getPayload();
        verify(payload).getEmail();

        verify(userService).findByEmail("taras@mail.com");

        verify(modelMapper).map(any(), eq(UserVO.class));
        verify(userRepo).save(any());
        verify(achievementService, times(2)).findAll();
    }

    @Test
    void authenticationThrowsUserDeactivatedExceptionTest() throws GeneralSecurityException, IOException {
        UserVO userVO = UserVO.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME)
            .role(Role.ROLE_USER).userStatus(UserStatus.DEACTIVATED)
            .lastActivityTime(LocalDateTime.now()).dateOfRegistration(LocalDateTime.now())
            .build();

        when(googleIdTokenVerifier.verify("idToken")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmail("test@mail.com")).thenReturn(userVO);

        assertThrows(UserDeactivatedException.class,
            () -> googleSecurityService.authenticate("idToken", "ua"));

        verify(googleIdTokenVerifier).verify("idToken");
        verify(googleIdToken, times(2)).getPayload();
        verify(payload).getEmail();
        verify(userService).findByEmail("test@mail.com");
    }

    @Test
    void authenticateThrowsIOExceptionTest() throws IOException {
        when(httpClient.execute(any(HttpGet.class))).thenThrow(IOException.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("accessToken", "ua"));

        assertEquals(ErrorMessage.BAD_GOOGLE_TOKEN + ". null", exception.getMessage());

        verify(httpClient).execute(any(HttpGet.class));
    }
}