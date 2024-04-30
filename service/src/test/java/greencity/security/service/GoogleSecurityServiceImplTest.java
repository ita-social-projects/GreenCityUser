package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.RestClient;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.AchievementService;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
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
    @InjectMocks
    GoogleSecurityServiceImpl googleSecurityService;

    /*
     * @Test void authenticateUserNotNullTest() throws GeneralSecurityException,
     * IOException { User user = ModelUtils.getUser(); UserVO userVO =
     * ModelUtils.getUserVO();
     * when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
     * when(googleIdToken.getPayload()).thenReturn(payload);
     * when(payload.getEmail()).thenReturn("test@mail.com");
     * when(userService.findByEmail("test@mail.com")).thenReturn(userVO);
     * SuccessSignInDto result = googleSecurityService.authenticate("1234", "ua");
     * assertEquals(user.getName(), result.getName()); assertEquals(user.getId(),
     * result.getUserId()); }
     */

    /*
     * @Test void authenticateNullUserTest() throws GeneralSecurityException,
     * IOException { UserVO userVO = ModelUtils.getUserVO(); User user =
     * ModelUtils.getUser(); List<Achievement> achievementList =
     * Collections.singletonList(ModelUtils.getAchievement()); List<AchievementVO>
     * achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
     * List<UserAchievement> userAchievementList =
     * Collections.singletonList(ModelUtils.getUserAchievement());
     * userVO.setId(null); userVO.setName(null); user.setId(null);
     * user.setName(null); user.setUserAchievements(userAchievementList);
     * when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
     * when(googleIdToken.getPayload()).thenReturn(payload);
     * when(payload.getEmail()).thenReturn("taras@mail.com");
     * when(userService.findByEmail("taras@mail.com")).thenReturn(null);
     * when(modelMapper.map(any(), eq(UserVO.class))).thenReturn(userVO);
     * when(userRepo.save(any())).thenReturn(user);
     * when(achievementService.findAll()).thenReturn(achievementVOList);
     * when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
     * }.getType())).thenReturn(achievementList); when(modelMapper.map(user,
     * UbsProfileCreationDto.class)).thenReturn(UbsProfileCreationDto.builder().
     * build());
     * when(restClient.createUbsProfile(any(UbsProfileCreationDto.class))).
     * thenReturn(1L); SuccessSignInDto result =
     * googleSecurityService.authenticate("1234", "ua");
     * assertNull(result.getUserId()); assertNull(result.getName()); }
     */

    @Test
    void authenticationThrowsIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> googleSecurityService.authenticate("1234", "ua"));
    }

    /*
     * @Test void authenticationThrowsUserDeactivatedExceptionTest() throws
     * GeneralSecurityException, IOException { User user = User.builder().id(1L)
     * .email(TestConst.EMAIL).name(TestConst.NAME).role(Role.ROLE_USER)
     * .userStatus(UserStatus.DEACTIVATED).lastActivityTime(LocalDateTime.now())
     * .dateOfRegistration(LocalDateTime.now()).build(); UserVO userVO =
     * UserVO.builder().id(1L).email(TestConst.EMAIL).name(TestConst.NAME)
     * .role(Role.ROLE_USER).userStatus(UserStatus.DEACTIVATED)
     * .lastActivityTime(LocalDateTime.now()).dateOfRegistration(LocalDateTime.now()
     * ) .build();
     * when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
     * when(googleIdToken.getPayload()).thenReturn(payload);
     * when(payload.getEmail()).thenReturn("test@mail.com");
     * when(userService.findByEmail("test@mail.com")).thenReturn(userVO);
     * assertThrows(UserDeactivatedException.class, () ->
     * googleSecurityService.authenticate("1234", "ua")); }
     */

    @Test
    void authenticationThrowsIllegalArgumentExceptionInCatchBlockTest()
        throws GeneralSecurityException, IOException {
        when(googleIdTokenVerifier.verify("1234")).thenThrow(GeneralSecurityException.class);
        assertThrows(IllegalArgumentException.class, () -> googleSecurityService.authenticate("1234", "ua"));
    }

}
