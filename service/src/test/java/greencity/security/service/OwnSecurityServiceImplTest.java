package greencity.security.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.CloudFlareClient;
import greencity.client.GreenCityRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.security.CloudFlareRequest;
import greencity.dto.security.CloudFlareResponse;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementCreateDto;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.ProjectName;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRefreshTokenException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.GreenCityServiceException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PasswordsDoNotMatchesException;
import greencity.exception.exceptions.UserAlreadyHasPasswordException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.UserProfileCreationException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.SetPasswordDto;
import greencity.security.dto.ownsecurity.TestersSignInRequest;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.EmailService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    OwnSecurityService ownSecurityService;

    private UserVO verifiedUser;
    private OwnSignInDto ownSignInDto;
    private UserVO notVerifiedUser;
    private UpdatePasswordDto updatePasswordDto;
    private UserManagementCreateDto userManagementCreateDto;
    private User userForBruteForceTest;
    private TestersSignInRequest request;

    @BeforeEach
    void init() {
        ownSecurityService = new OwnSecurityServiceImpl(ownSecurityRepo, positionRepo, userService, passwordEncoder,
            jwtTool, restorePasswordEmailRepo, modelMapper, userRepo, emailService, authorityRepo,
            loginAttemptService, greenCityRemoteClient);

        ReflectionTestUtils.setField(ownSecurityService, "expirationTime", 1);
        ReflectionTestUtils.setField(ownSecurityService, "secretKey", "secret-key");

        verifiedUser = UserVO.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.VERIFIED)
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
            .userStatus(UserStatus.CREATED)
            .verifyEmail(new VerifyEmailVO())
            .ownSecurity(OwnSecurityVO.builder().password("password").build())
            .role(Role.ROLE_USER)
            .build();
        updatePasswordDto = UpdatePasswordDto.builder()
            .password("newPassword")
            .confirmPassword("newPassword")
            .build();
        userManagementCreateDto = UserManagementCreateDto.builder()
            .name(TestConst.NAME)
            .email(TestConst.EMAIL)
            .role(Role.ROLE_USER)
            .build();
        userForBruteForceTest = User.builder()
            .id(1L)
            .email("test@somemail.com")
            .name("Test")
            .language(Language.builder()
                .id(1L)
                .code("en")
                .build())
            .userStatus(UserStatus.VERIFIED)
            .build();
        request = ModelUtils.getTestersSignInRequest();
    }

    @Test
    void signUp() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();

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

        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(3)).map(any(), any());
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

        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(3)).map(any(), any());
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

        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(EmployeeSignUpDto.class), eq(OwnSignUpDto.class))).thenReturn(ownSignUpDto);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn("New-token-key");

        ownSecurityService.signUpEmployee(employeeSignUpDto, "en");

        verify(modelMapper, times(3)).map(any(), any());
        verify(userRepo).save(any());
        verify(jwtTool, times(1)).generateTokenKeyWithCodedDate();
        verify(jwtTool, times(1)).generateTokenKey();
    }

    @Test
    void signUpWithDuplicatedEmployee() {
        UserVO userVO = ModelUtils.getUserVO();
        EmployeeSignUpDto employeeSignUpDto = ModelUtils.getEmployeeSignUpDto();
        OwnSignUpDto ownSignUpDto = ModelUtils.getOwnSignUpDto();

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
        UserVO userVO = UserVO.builder().verifyEmail(new VerifyEmailVO()).build();

        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(userRepo.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.signUp(ownSignUpDto, "en"));
    }

    @Test
    void signIn() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
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
        verify(loginAttemptService, times(1)).isBlockedByWrongPassword(anyString());
    }

    @Test
    void signInNotVerifiedUser() {
        when(userService.findByEmail(anyString())).thenReturn(notVerifiedUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(UserVO.class))).thenReturn("new-refresh-token");
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
            .userStatus(UserStatus.VERIFIED)
            .ownSecurity(null)
            .role(Role.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(false);
        when(cloudFlareClient.getCloudFlareResponse(any(CloudFlareRequest.class)))
            .thenReturn(new CloudFlareResponse(true, null, null, null));

        assertThrows(WrongPasswordException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void updateAccessTokensTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(true);
        ownSecurityService.updateAccessTokens("12345", ProjectName.PICKUP);
        verify(jwtTool).createAccessToken(verifiedUser.getEmail(), verifiedUser.getRole());
        verify(jwtTool).createRefreshToken(verifiedUser);
    }

    @Test
    void updateAccessTokensBadRefreshTokenExceptionTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenThrow(ExpiredJwtException.class);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345", ProjectName.PICKUP));
    }

    @Test
    void updateAccessTokensBadRefreshTokenTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(false);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345", ProjectName.PICKUP));
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
        user.setUserStatus(UserStatus.CREATED);
        user.setLanguage(Language.builder().id(2L).code("en").build());
        user.setDateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47));

        UserAdminRegistrationDto dto = ModelUtils.getUserAdminRegistrationDto();
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any())).thenReturn(user);
        when(modelMapper.map(user, UserAdminRegistrationDto.class)).thenReturn(dto);

        UserAdminRegistrationDto expected = ownSecurityService.managementRegisterUser(userManagementCreateDto);

        assertEquals(dto, expected);

        verify(restorePasswordEmailRepo, times(1)).save(any());
        verify(emailService).sendApprovalEmail(1L, TestConst.NAME, TestConst.EMAIL, "token-key");
    }

    @Test
    void managementRegisterUserShouldThrowUserAlreadyRegisteredException() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(ModelUtils.getUser()));

        Exception thrown = assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.managementRegisterUser(userManagementCreateDto));

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
    void singInBlockedUserByPassword() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(loginAttemptService.isBlockedByWrongPassword(anyString())).thenReturn(true);
        when(userRepo.findByEmail(anyString()))
            .thenReturn(Optional.ofNullable(userForBruteForceTest));

        assertThrows(WrongPasswordException.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void testersSignInTest() {
        UserVO userVO = ModelUtils.getUserVOWithData();
        userVO.setVerifyEmail(null);

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ownSecurityService.testersSignIn(request);

        verify(userService).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void testersSignInTestWrongEmailException() {
        when(userService.findByEmail(anyString())).thenReturn(null);

        assertThrows(WrongEmailException.class, () -> ownSecurityService.testersSignIn(request));

        verify(userService).findByEmail(anyString());
    }

    @Test
    void testersSignInTestWrongPasswordException() {
        UserVO userVO = ModelUtils.getUserVOWithData();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> ownSecurityService.testersSignIn(request));

        verify(userService).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void testersSignInTestWrongPasswordExceptionWithNullOwnSecurity() {
        UserVO userVO = ModelUtils.getUserVOWithData();
        userVO.setOwnSecurity(null);

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        assertThrows(WrongPasswordException.class, () -> ownSecurityService.testersSignIn(request));

        verify(userService).findByEmail(anyString());
    }

    @Test
    void testersSignInTestBadRequestExceptionWithInvalidSecretKey() {
        UserVO userVO = ModelUtils.getUserVOWithData();
        TestersSignInRequest testersSignInRequest = ModelUtils.getTestersSignInRequestWithInvalidSecretKey();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> ownSecurityService.testersSignIn(testersSignInRequest));

        verify(userService).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void testersSignInTestWithEmailNotVerifiedException() {
        UserVO userVO = ModelUtils.getUserVOWithData();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(EmailNotVerified.class, () -> ownSecurityService.testersSignIn(request));

        verify(userService).findByEmail(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void createExternalUserProfilesTest() {
        User user = ModelUtils.getUser();
        UbsProfileCreationDto ubsProfileDto = ModelUtils.getUbsProfileCreationDto();

        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(ubsProfileDto);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(greenCityRemoteClient.createUbsProfile(ubsProfileDto)).thenReturn(1L);

        ownSecurityService.createExternalUserProfiles(user.getId());

        verify(userRepo).findById(user.getId());
        verify(modelMapper).map(user, UbsProfileCreationDto.class);
        verify(greenCityRemoteClient).createUbsProfile(ubsProfileDto);
        verify(userService).createGreenCityUser(user.getId(), null);
        verify(userRepo, never()).delete(any(User.class));
    }

    @Test
    void createExternalUserProfilesWhenUserNotFoundTest() {
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> ownSecurityService.createExternalUserProfiles(userId));

        verify(userRepo).findById(userId);
        verify(greenCityRemoteClient, never()).createUbsProfile(any());
        verify(userService, never()).createGreenCityUser(anyLong(), any());
    }

    @ParameterizedTest
    @MethodSource("provideExceptions")
    void createExternalUserProfilesWhenServiceUnavailableTest(RuntimeException exception) {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        UbsProfileCreationDto ubsProfileDto = ModelUtils.getUbsProfileCreationDto();

        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(ubsProfileDto);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(greenCityRemoteClient.createUbsProfile(ubsProfileDto)).thenThrow(exception);

        UserProfileCreationException result = assertThrows(UserProfileCreationException.class,
            () -> ownSecurityService.createExternalUserProfiles(userId));

        String expectedMessage = String.format("Ubs profile has not been created for user with uuid %s.",
            user.getUuid());
        assertEquals(expectedMessage, result.getMessage());

        verify(userRepo).findById(userId);
        verify(modelMapper).map(user, UbsProfileCreationDto.class);
        verify(greenCityRemoteClient).createUbsProfile(ubsProfileDto);
        verify(userRepo).delete(user);
        verify(userService, never()).createGreenCityUser(anyLong(), any());
    }

    private static Stream<Arguments> provideExceptions() {
        return Stream.of(
            Arguments.of(new WebClientRequestException(
                new RuntimeException("Connection refused"),
                HttpMethod.POST,
                URI.create("http://external-service"),
                HttpHeaders.EMPTY), "WebClientRequestException"),
            Arguments.of(new GreenCityServiceException("Green City service error"),
                "GreenCityServiceException"));
    }
}
