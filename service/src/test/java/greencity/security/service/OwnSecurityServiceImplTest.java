package greencity.security.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.CloudFlareClient;
import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.security.CloudFlareRequest;
import greencity.dto.security.CloudFlareResponse;
import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import greencity.security.dto.ownsecurity.*;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.EmailService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OwnSecurityServiceImplTest {

    @Mock
    OwnSecurityRepo ownSecurityRepo;

    @Mock
    PositionRepo positionRepo;

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTool jwtTool;

    @Mock
    RestorePasswordEmailRepo restorePasswordEmailRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepo userRepo;

    @Mock
    EmailService emailService;

    @Mock
    AuthorityRepo authorityRepo;

    @Mock
    LoginAttemptService loginAttemptService;

    @Mock
    CloudFlareClient cloudFlareClient;

    private OwnSecurityService ownSecurityService;

    private UserVO verifiedUser;
    private OwnSignInDto ownSignInDto;
    private UserVO notVerifiedUser;
    private UpdatePasswordDto updatePasswordDto;
    private UserManagementDto userManagementDto;
    private User userForBruteForceTest;

    @BeforeEach
    public void init() {
        ownSecurityService = new OwnSecurityServiceImpl(ownSecurityRepo, positionRepo, userService, passwordEncoder,
            jwtTool, restorePasswordEmailRepo, modelMapper, userRepo, emailService, authorityRepo,
            loginAttemptService, cloudFlareClient);

        ReflectionTestUtils.setField(ownSecurityService, "expirationTime", 1);

        verifiedUser = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();
        ownSignInDto = OwnSignInDto.builder()
            .email("test@gmail.com")
            .password("password")
            .build();
        notVerifiedUser = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .verifyEmail(new VerifyEmailVO())
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();
        updatePasswordDto = UpdatePasswordDto.builder()
            .password("newPassword")
            .confirmPassword("newPassword")
            .build();
        userManagementDto = UserManagementDto.builder()
            .name(TestConst.NAME)
            .email(TestConst.EMAIL)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.BLOCKED)
            .build();
        userForBruteForceTest = User.builder()
            .id(1L)
            .email("test@somemail.com")
            .name("Test")
            .language(Language.builder()
                .id(1L)
                .code("en")
                .build())
            .userStatus(UserStatus.ACTIVATED)
            .build();
    }

    @Test
    void signUp() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);
        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        ownSecurityService.signUp(new OwnSignUpDto(), "en");
        verify(emailService, times(1)).sendVerificationEmail(
            refEq(user.getId()),
            refEq(user.getName()),
            refEq(user.getEmail()),
            refEq(user.getVerifyEmail().getToken()),
            refEq("en"), eq(false));
        verify(jwtTool, times(2)).generateTokenKey();
    }

    @Test
    void signUpEmployeeTest_PositionInUa() {
        User user = ModelUtils.getUserWithUbsRole();
        UserVO userVO = ModelUtils.getUserVO();
        EmployeeSignUpDto employeeSignUpDto = ModelUtils.getEmployeeSignUpDto_UA();
        OwnSignUpDto ownSignUpDto = ModelUtils.getOwnSignUpDto();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);
        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(2)).map(any(), any());
        verify(userRepo).save(any());
        verify(jwtTool, times(1)).generateTokenKeyWithCodedDate();
        verify(jwtTool, times(1)).generateTokenKey();
    }

    @Test
    void signUpEmployeeTest() {
        User user = ModelUtils.getUserWithUbsRole();
        UserVO userVO = ModelUtils.getUserVO();
        EmployeeSignUpDto employeeSignUpDto = ModelUtils.getEmployeeSignUpDto();
        employeeSignUpDto.setPositions(Collections.emptyList());
        OwnSignUpDto ownSignUpDto = ModelUtils.getOwnSignUpDto();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);

        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(2)).map(any(), any());
        verify(userRepo).save(any());
        verify(jwtTool, times(1)).generateTokenKeyWithCodedDate();
        verify(jwtTool, times(1)).generateTokenKey();
    }

    @Test
    void signUpEmployeeTest_PositionInEn() {
        User user = ModelUtils.getUserWithUbsRole();
        UserVO userVO = ModelUtils.getUserVO();
        EmployeeSignUpDto employeeSignUpDto = ModelUtils.getEmployeeSignUpDto_EN();
        employeeSignUpDto.setPositions(Collections.emptyList());
        OwnSignUpDto ownSignUpDto = ModelUtils.getOwnSignUpDto();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);

        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(2)).map(any(), any());
        verify(userRepo).save(any());
        verify(jwtTool, times(1)).generateTokenKeyWithCodedDate();
        verify(jwtTool, times(1)).generateTokenKey();
    }

    @Test
    void signUpWithDuplicatedEmployee() {
        User user = ModelUtils.getUserWithUbsRole();
        UserVO userVO = ModelUtils.getUserVO();
        EmployeeSignUpDto employeeSignUpDto = ModelUtils.getEmployeeSignUpDto();
        OwnSignUpDto ownSignUpDto = ModelUtils.getOwnSignUpDto();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);

        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);

        when(userRepo.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.signUpEmployee(employeeSignUpDto, "en"));

        verify(jwtTool, times(1)).generateTokenKey();
        verify(jwtTool, times(1)).generateTokenKeyWithCodedDate();
    }

    @Test
    void signUpThrowsUserAlreadyRegisteredExceptionTest() {
        OwnSignUpDto ownSignUpDto = new OwnSignUpDto();
        User user = User.builder().verifyEmail(new VerifyEmail()).build();
        UserVO userVO = UserVO.builder().verifyEmail(new VerifyEmailVO()).build();
        List<Achievement> achievementList = Collections.singletonList(ModelUtils.getAchievement());
        List<AchievementVO> achievementVOList = Collections.singletonList(ModelUtils.getAchievementVO());
        List<UserAchievement> userAchievementList = Collections.singletonList(ModelUtils.getUserAchievement());
        user.setUserAchievements(userAchievementList);
        when(modelMapper.map(achievementVOList, new TypeToken<List<Achievement>>() {
        }.getType())).thenReturn(achievementList);
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(userRepo.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.signUp(ownSignUpDto, "en"));
    }

    @Test
    void signIn() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(false);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(false);
        when(cloudFlareClient.getCloudFlareResponse(any(CloudFlareRequest.class)))
            .thenReturn(new CloudFlareResponse(true, null, null, null));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(UserVO.class))).thenReturn("new-refresh-token");

        ownSecurityService.signIn(ownSignInDto);

        verify(userService, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtTool, times(1)).createAccessToken(anyString(), any(Role.class));
        verify(jwtTool, times(1)).createRefreshToken(any(UserVO.class));
        verify(loginAttemptService, times(1)).isBlockedByCaptcha(anyString());
        verify(loginAttemptService, times(1)).isBlockedByWrongPassword(anyString());
        verify(cloudFlareClient, times(1)).getCloudFlareResponse(any(CloudFlareRequest.class));
    }

    @Test
    void signInNotVerifiedUser() {
        when(userService.findByEmail(anyString())).thenReturn(notVerifiedUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(UserVO.class))).thenReturn("new-refresh-token");
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(false);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(false);
        when(cloudFlareClient.getCloudFlareResponse(any(CloudFlareRequest.class)))
            .thenReturn(new CloudFlareResponse(true, null, null, null));

        assertThrows(EmailNotVerified.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInNullUserTest() {
        when(userService.findByEmail("test@gmail.com")).thenReturn(null);
        assertThrows(WrongEmailException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInWrongPasswordTest() {
        UserVO user = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .ownSecurity(null)
            .role(Role.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(false);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(false);
        when(cloudFlareClient.getCloudFlareResponse(any(CloudFlareRequest.class)))
            .thenReturn(new CloudFlareResponse(true, null, null, null));

        assertThrows(WrongPasswordException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInDeactivatedUserTest() {
        UserVO user = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.DEACTIVATED)
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        Exception exception = assertThrows(BadUserStatusException.class, () -> ownSecurityService.signIn(ownSignInDto));

        String expectedMessage = ErrorMessage.USER_DEACTIVATED;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void signInBlockedUserTest() {
        UserVO user = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.BLOCKED)
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        Exception exception = assertThrows(BadUserStatusException.class, () -> ownSecurityService.signIn(ownSignInDto));

        String expectedMessage = ErrorMessage.USER_BLOCKED;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void signInCreatedUserTest() {
        UserVO user = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.CREATED)
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();

        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        Exception exception = assertThrows(BadUserStatusException.class, () -> ownSecurityService.signIn(ownSignInDto));

        String expectedMessage = ErrorMessage.USER_CREATED;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void signInDeletedUserTest() {
        UserVO user = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.DELETED)
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();

        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        Exception exception = assertThrows(BadUserStatusException.class, () -> ownSecurityService.signIn(ownSignInDto));

        String expectedMessage = ErrorMessage.USER_DELETED;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateAccessTokensTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(true);
        ownSecurityService.updateAccessTokens("12345");
        verify(jwtTool).createAccessToken(verifiedUser.getEmail(), verifiedUser.getRole());
        verify(jwtTool).createRefreshToken(verifiedUser);
    }

    @Test
    void updateAccessTokensBadRefreshTokenExceptionTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenThrow(ExpiredJwtException.class);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensBadRefreshTokenTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(false);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensBlockedUserTest() {
        verifiedUser.setUserStatus(UserStatus.BLOCKED);
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(UserBlockedException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensDeactivatedUserTest() {
        verifiedUser.setUserStatus(UserStatus.DEACTIVATED);
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(UserDeactivatedException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateCurrentPasswordTest() {
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(passwordEncoder.encode(updatePasswordDto.getPassword())).thenReturn(updatePasswordDto.getPassword());
        ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com");
        verify(ownSecurityRepo).updatePassword(updatePasswordDto.getPassword(), 1L);
    }

    @Test
    void updateCurrentPasswordDifferentPasswordsTest() {
        updatePasswordDto.setPassword("123");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(PasswordsDoNotMatchesException.class,
            () -> ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com"));
    }

    @Test
    void updateCurrentPasswordEmailNotVerifiedTest() {
        updatePasswordDto.setPassword("123");

        UserVO user = ModelUtils.getUserVO();
        user.setUserStatus(UserStatus.CREATED);

        when(userService.findByEmail("test@gmail.com")).thenReturn(user);

        assertThrows(EmailNotVerified.class,
            () -> ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com"));
    }

    @Test
    void managementRegisterUserTest() {
        User user = ModelUtils.getUser();
        user.setUserStatus(UserStatus.BLOCKED);
        user.setLanguage(Language.builder().id(2L).code("en").build());
        user.setDateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47));

        UserAdminRegistrationDto dto = ModelUtils.getUserAdminRegistrationDto();
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any())).thenReturn(user);
        when(modelMapper.map(user, UserAdminRegistrationDto.class)).thenReturn(dto);

        UserAdminRegistrationDto expected = ownSecurityService.managementRegisterUser(userManagementDto);

        assertEquals(dto, expected);

        verify(restorePasswordEmailRepo, times(1)).save(any());
        verify(emailService).sendApprovalEmail(1L, TestConst.NAME, TestConst.EMAIL, "token-key");
    }

    @Test
    void managementRegisterUserShouldThrowUserAlreadyRegisteredException() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(ModelUtils.getUser()));

        Exception thrown = assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.managementRegisterUser(userManagementDto));

        assertEquals(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL, thrown.getMessage());
    }

    @Test
    void hasPasswordTrue() {
        User user = ModelUtils.getUser();
        user.setOwnSecurity(ModelUtils.TEST_OWN_SECURITY);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertTrue(ownSecurityService.hasPassword(user.getEmail()));
    }

    @Test
    void hasPasswordFalse() {
        User user = ModelUtils.getUser();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertFalse(ownSecurityService.hasPassword(user.getEmail()));
    }

    @Test
    void hasPasswordWrongEmailException() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(WrongEmailException.class, () -> ownSecurityService.hasPassword(""));
    }

    @Test
    void setPassword() {
        SetPasswordDto dto = SetPasswordDto.builder()
            .password(ModelUtils.TEST_OWN_RESTORE_DTO.getPassword())
            .confirmPassword(ModelUtils.TEST_OWN_RESTORE_DTO.getConfirmPassword())
            .build();
        User user = ModelUtils.getUser();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ownSecurityService.setPassword(dto, user.getEmail());

        assertNotNull(user.getOwnSecurity());
        verify(userRepo).save(user);
    }

    @Test
    void setPasswordWrongEmailException() {
        SetPasswordDto dto = SetPasswordDto.builder().build();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(WrongEmailException.class, () -> ownSecurityService.setPassword(dto, ""));
    }

    @Test
    void setPasswordUserAlreadyHasPasswordException() {
        SetPasswordDto dto = SetPasswordDto.builder().build();
        User user = ModelUtils.getUser();
        user.setOwnSecurity(ModelUtils.TEST_OWN_SECURITY);
        String email = user.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyHasPasswordException.class, () -> ownSecurityService.setPassword(dto, email));
    }

    @Test
    void setPasswordPasswordsDoNotMatchesException() {
        SetPasswordDto dto = SetPasswordDto.builder()
            .password(ModelUtils.TEST_OWN_RESTORE_DTO_WRONG.getPassword())
            .confirmPassword(ModelUtils.TEST_OWN_RESTORE_DTO_WRONG.getConfirmPassword())
            .build();
        User user = ModelUtils.getUser();
        String email = user.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(PasswordsDoNotMatchesException.class, () -> ownSecurityService.setPassword(dto, email));
    }

    @Test
    void deleteUserByEmailTest() {
        User deactivatedUser = User.builder()
            .id(1L)
            .email("test@somemail.com")
            .userStatus(UserStatus.ACTIVATED)
            .build();
        Optional<User> optionalUser = Optional.of(deactivatedUser);

        when(userRepo.findByEmail(deactivatedUser.getEmail())).thenReturn(optionalUser);

        ownSecurityService.deleteUserByEmail(deactivatedUser.getEmail());
        assertEquals(UserStatus.DELETED, deactivatedUser.getUserStatus());

        verify(userRepo, times(1)).findByEmail(deactivatedUser.getEmail());
        verify(userRepo, times(1)).save(deactivatedUser);
    }

    @Test
    void deleteUserByEmail_UserNotExists() {
        String nonExistUserEmail = "nonexistent@somemail.com";

        when(userRepo.findByEmail(nonExistUserEmail)).thenReturn(Optional.empty());

        assertThrows(WrongEmailException.class, () -> ownSecurityService.deleteUserByEmail(nonExistUserEmail));
    }

    @Test
    void deleteUserByEmailNotVerifiedUserTest() {
        User newNotVerifiedUser = User.builder()
            .id(1L)
            .email("test@somemail.com")
            .userStatus(UserStatus.DEACTIVATED)
            .build();
        Optional<User> optionalUser = Optional.of(newNotVerifiedUser);
        String email = newNotVerifiedUser.getEmail();

        when(userRepo.findByEmail(newNotVerifiedUser.getEmail())).thenReturn(optionalUser);
        assertThrows(EmailNotVerified.class, () -> ownSecurityService.deleteUserByEmail(email));

        verify(userRepo, times(1)).findByEmail(newNotVerifiedUser.getEmail());
    }

    @Test
    void singInBlockedUser() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(true);
        when(userRepo.findByEmail(anyString()))
            .thenReturn(Optional.ofNullable(userForBruteForceTest));

        assertThrows(UserBlockedException.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void singInBlockedUserByPassword() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(false);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(true);
        when(userRepo.findByEmail(anyString()))
            .thenReturn(Optional.ofNullable(userForBruteForceTest));

        assertThrows(WrongPasswordException.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void throwExceptionWhenCaptchaIsNotValid() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(loginAttemptService.isBlockedByCaptcha(anyString())).thenReturn(false);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(false);
        when(userRepo.findByEmail(anyString()))
            .thenReturn(Optional.ofNullable(userForBruteForceTest));
        when(cloudFlareClient.getCloudFlareResponse(any(CloudFlareRequest.class)))
            .thenReturn(new CloudFlareResponse(false, null, null, null));

        assertThrows(WrongCaptchaException.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void unblockUserTest() {
        when(jwtTool.getEmailOutOfAccessToken(anyString())).thenReturn("test@mail.com");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(userForBruteForceTest));

        ownSecurityService.unblockAccount("test@mail.com");

        verify(jwtTool, times(1)).getEmailOutOfAccessToken(anyString());
        verify(userRepo, times(1)).findByEmail(anyString());
        verify(userRepo, times(1)).save(userForBruteForceTest);
    }

    @Test
    void unblockUserWithInvalidToken() {
        when(jwtTool.getEmailOutOfAccessToken(anyString())).thenThrow(IllegalArgumentException.class);

        assertThrows(BadRequestException.class, () -> ownSecurityService.unblockAccount("test@mail.com"));
    }
}
