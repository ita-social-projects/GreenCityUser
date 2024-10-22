package greencity.security.service;

import greencity.client.CloudFlareClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.security.CloudFlareRequest;
import greencity.dto.security.CloudFlareResponse;
import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.OwnSecurity;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import greencity.entity.VerifyEmail;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRefreshTokenException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PasswordsDoNotMatchesException;
import greencity.exception.exceptions.UserAlreadyHasPasswordException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.exception.exceptions.WrongCaptchaException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.SetPasswordDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.EmailService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code OwnSecurityService}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OwnSecurityServiceImpl implements OwnSecurityService {
    private static final String VALID_PW_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>?,./";
    private final OwnSecurityRepo ownSecurityRepo;
    private final PositionRepo positionRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final AuthorityRepo authorityRepo;
    private final LoginAttemptService loginAttemptService;
    private final CloudFlareClient cloudFlareClient;
    @Value("${verifyEmailTimeHour}")
    private Integer expirationTime;
    @Value("${bruteForceSettings.blockTimeInMinutes}")
    private String blockTimeInMinutes;
    @Value("${cloud-flare.secret-key}")
    private String cloudFlareSecretKey;

    /**
     * {@inheritDoc}
     *
     * @return {@link SuccessSignUpDto}
     */
    @Transactional
    @Override
    public SuccessSignUpDto signUp(OwnSignUpDto dto, String language) {
        User user = createNewRegisteredUser(dto, jwtTool.generateTokenKey(), language);
        user.setOwnSecurity(createOwnSecurity(dto, user));
        user.setVerifyEmail(createVerifyEmail(user, jwtTool.generateTokenKey()));
        user.setUuid(UUID.randomUUID().toString());
        Set<UserNotificationPreference> userNotificationPreferences = Arrays.stream(EmailPreference.values())
            .map(emailPreference -> UserNotificationPreference.builder()
                .user(user)
                .emailPreference(emailPreference)
                .periodicity(EmailPreferencePeriodicity.TWICE_A_DAY)
                .build())
            .collect(Collectors.toSet());
        user.setNotificationPreferences(userNotificationPreferences);
        try {
            User savedUser = userRepo.save(user);
            user.setId(savedUser.getId());
            emailService.sendVerificationEmail(savedUser.getId(), savedUser.getName(), savedUser.getEmail(),
                savedUser.getVerifyEmail().getToken(), language, dto.isUbs());
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
        user.setShowLocation(true);
        user.setShowEcoPlace(true);
        user.setShowShoppingList(true);
        return new SuccessSignUpDto(user.getId(), user.getName(), user.getEmail(), true);
    }

    private User createNewRegisteredUser(OwnSignUpDto dto, String refreshTokenKey, String language) {
        return User.builder()
            .name(dto.getName())
            .firstName(dto.getName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(Role.ROLE_USER)
            .refreshTokenKey(refreshTokenKey)
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.CREATED)
            .emailNotification(EmailNotification.DISABLED)
            .rating(AppConstant.DEFAULT_RATING)
            .language(Language.builder()
                .id(modelMapper.map(language, Long.class))
                .build())
            .build();
    }

    private RestorePasswordEmail createRestorePasswordEmail(User user, String emailVerificationToken) {
        return RestorePasswordEmail.builder()
            .user(user)
            .token(emailVerificationToken)
            .expiryDate(calculateExpirationDateTime())
            .build();
    }

    private OwnSecurity createOwnSecurity(OwnSignUpDto dto, User user) {
        return OwnSecurity.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .user(user)
            .build();
    }

    private VerifyEmail createVerifyEmail(User user, String emailVerificationToken) {
        return VerifyEmail.builder()
            .user(user)
            .token(emailVerificationToken)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    public SuccessSignUpDto signUpEmployee(EmployeeSignUpDto employeeSignUpDto, String language) {
        String password = generatePassword();
        employeeSignUpDto.setPassword(password);
        OwnSignUpDto dto = modelMapper.map(employeeSignUpDto, OwnSignUpDto.class);
        User employee = createNewRegisteredUser(dto, jwtTool.generateTokenKey(), language);
        employee.setOwnSecurity(createOwnSecurity(dto, employee));
        employee.setRole(Role.ROLE_UBS_EMPLOYEE);
        employee.setRestorePasswordEmail(createRestorePasswordEmail(employee, jwtTool.generateTokenKeyWithCodedDate()));
        employee.setUuid(employeeSignUpDto.getUuid());
        employee.setShowLocation(true);
        employee.setShowEcoPlace(true);
        employee.setShowShoppingList(true);
        List<String> positionNames = employeeSignUpDto.getPositions().stream()
            .flatMap(position -> Stream.of(position.getName(), position.getNameEn()))
            .toList();
        employee.setAuthorities(authorityRepo.findAuthoritiesByPositions(positionNames));
        employee.setPositions(positionRepo.findPositionsByNames(positionNames));

        try {
            User savedUser = userRepo.save(employee);
            employee.setId(savedUser.getId());
            emailService.sendCreateNewPasswordForEmployee(savedUser.getId(), savedUser.getFirstName(),
                employee.getEmail(), savedUser.getRestorePasswordEmail().getToken(), language, dto.isUbs());
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }

        return new SuccessSignUpDto(employee.getId(), employee.getName(), employee.getEmail(), true);
    }

    private LocalDateTime calculateExpirationDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(this.expirationTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto signIn(final OwnSignInDto dto) {
        String email = dto.getEmail();
        UserVO user = validateUser(dto);

        handleUserStatus(user.getUserStatus());
        handleBruteForceProtection(email);

        verifyCaptcha(dto, email);

        validatePassword(dto, user);

        if (!isEmailVerified(user)) {
            throw new EmailNotVerified("You should verify the email first, check your email box!");
        }

        return createSuccessSignInResponse(user, email);
    }

    private UserVO validateUser(final OwnSignInDto dto) {
        UserVO user = userService.findByEmail(dto.getEmail());
        if (user == null) {
            throw new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail());
        }
        return user;
    }

    /**
     * Checks if user is blocked by brute-force protection (captcha or wrong
     * password). If user is blocked, logs error and blocks user by email. If user
     * exceeded wrong password attempts, throws WrongPasswordException.
     *
     * @param email user email
     */
    private void handleBruteForceProtection(String email) {
        if (loginAttemptService.isBlockedByCaptcha(email)) {
            log.error("Brute force protection, user with email is blocked - {}", email);
            blockUserByEmail(email);
        }

        if (loginAttemptService.isBlockedByWrongPassword(email)) {
            log.error("Too many failed login attempts - {}, account is blocked for {} minutes", email,
                blockTimeInMinutes);
            throw new WrongPasswordException(
                String.format(ErrorMessage.BRUTEFORCE_PROTECTION_MESSAGE_WRONG_PASS, blockTimeInMinutes));
        }
    }

    /**
     * Checks if captcha is valid. If captcha is not valid, logs error, increments
     * wrong captcha attempts and throws WrongCaptchaException.
     *
     * @param dto   - {@link OwnSignInDto} that have sign-in information
     * @param email - user email
     */
    private void verifyCaptcha(final OwnSignInDto dto, String email) {
        if (!getCloudFlareResponse(dto).success()) {
            loginAttemptService.loginFailedByCaptcha(email);
            throw new WrongCaptchaException(ErrorMessage.WRONG_CAPTCHA);
        }
    }

    /**
     * Validates password for user. If password is not correct, logs error,
     * increments wrong password attempts and throws WrongPasswordException.
     *
     * @param dto  - {@link OwnSignInDto} that have sign-in information
     * @param user - user with password to be validated
     */
    private void validatePassword(final OwnSignInDto dto, UserVO user) {
        if (!isPasswordCorrect(dto, user)) {
            loginAttemptService.loginFailedByWrongPassword(dto.getEmail());
            throw new WrongPasswordException(ErrorMessage.BAD_PASSWORD);
        }
    }

    /**
     * Checks if user has verified email. User is considered verified if there is no
     * VerifyEmail entity associated with his/her account.
     *
     * @param user - user to be checked
     * @return true if user has verified email, false otherwise
     */
    private boolean isEmailVerified(UserVO user) {
        return user.getVerifyEmail() == null;
    }

    /**
     * Creates a {@link SuccessSignInDto} that is used to sign in user. Creates a
     * new access token and a new refresh token and returns them in the
     * {@link SuccessSignInDto} object.
     *
     * @param user  user that is being signed in
     * @param email user's email
     * @return {@link SuccessSignInDto} with access token, refresh token and user's
     *         name
     */
    private SuccessSignInDto createSuccessSignInResponse(UserVO user, String email) {
        String accessToken = jwtTool.createAccessToken(email, user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), true);
    }

    /**
     * Blocks user by email. Sets user status to {@link UserStatus#BLOCKED}, saves
     * user and logs info about blocking. Then sends email with link to unblock and
     * restore password page and throws {@link UserBlockedException} with message
     * that contains time for which account is blocked.
     *
     * @param email email of user to be blocked
     * @throws UserBlockedException if user is blocked
     * @throws NotFoundException    if user with given email is not found
     */
    private void blockUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));

        user.setUserStatus(UserStatus.BLOCKED);
        userRepo.save(user);
        log.info("User with email {} is blocked", user.getEmail());

        emailService.sendBlockAccountNotificationWithUnblockLinkEmail(
            user.getId(), user.getName(), user.getEmail(),
            jwtTool.generateUnblockToken(email), getLanguageFromUser(user), false);

        throw new UserBlockedException(ErrorMessage.BRUTEFORCE_PROTECTION_MESSAGE);
    }

    /**
     * Calls CloudFlare api to check if given captcha is valid.
     *
     * @param dto - {@link OwnSignInDto} that contains captcha token
     * @return {@link CloudFlareResponse} with result of captcha validation
     */
    private CloudFlareResponse getCloudFlareResponse(OwnSignInDto dto) {
        return cloudFlareClient.getCloudFlareResponse(CloudFlareRequest.builder()
            .secret(cloudFlareSecretKey)
            .response(dto.getCaptchaToken())
            .build());
    }

    /**
     * Gets user language from user object. If user language code is "1", method
     * returns "ua", otherwise - "en".
     *
     * @param user user to get language from
     * @return "ua" or "en" depending on user language code
     */
    private String getLanguageFromUser(User user) {
        return user.getLanguage().getCode().equals("1") ? "ua" : "en";
    }

    private boolean isPasswordCorrect(OwnSignInDto signInDto, UserVO user) {
        if (user.getOwnSecurity() == null) {
            return false;
        }
        return passwordEncoder.matches(signInDto.getPassword(), user.getOwnSecurity().getPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AccessRefreshTokensDto updateAccessTokens(String refreshToken) {
        String email;
        try {
            email = jwtTool.getEmailOutOfAccessToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BadRefreshTokenException(ErrorMessage.REFRESH_TOKEN_NOT_VALID);
        }
        UserVO user = userService.findByEmail(email);
        checkUserStatus(user);
        String newRefreshTokenKey = jwtTool.generateTokenKey();
        userService.updateUserRefreshToken(newRefreshTokenKey, user.getId());
        if (jwtTool.isTokenValid(refreshToken, user.getRefreshTokenKey())) {
            user.setRefreshTokenKey(newRefreshTokenKey);
            return new AccessRefreshTokensDto(
                jwtTool.createAccessToken(user.getEmail(), user.getRole()),
                jwtTool.createRefreshToken(user));
        }
        throw new BadRefreshTokenException(ErrorMessage.REFRESH_TOKEN_NOT_VALID);
    }

    private void checkUserStatus(UserVO user) {
        UserStatus status = user.getUserStatus();
        if (status == UserStatus.BLOCKED) {
            throw new UserBlockedException(ErrorMessage.USER_DEACTIVATED);
        } else if (status == UserStatus.DEACTIVATED) {
            throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateCurrentPassword(UpdatePasswordDto updatePasswordDto, String email) {
        UserVO user = userService.findByEmail(email);

        if (user.getUserStatus() != UserStatus.ACTIVATED) {
            throw new EmailNotVerified(ErrorMessage.USER_EMAIL_IS_NOT_VERIFIED);
        }

        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchesException(ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }
        updatePassword(updatePasswordDto.getPassword(), user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserAdminRegistrationDto managementRegisterUser(UserManagementDto dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
        User user = managementCreateNewRegisteredUser(dto, jwtTool.generateTokenKey());
        OwnSecurity ownSecurity = managementCreateOwnSecurity(user);
        user.setOwnSecurity(ownSecurity);
        return modelMapper.map(
            savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey()), UserAdminRegistrationDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (user.getUserStatus() != UserStatus.ACTIVATED) {
            throw new EmailNotVerified(ErrorMessage.USER_EMAIL_IS_NOT_VERIFIED);
        }

        user.setUserStatus(UserStatus.DELETED);
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void unblockAccount(String token) {
        String email;
        try {
            email = jwtTool.getEmailOutOfAccessToken(token);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorMessage.TOKEN_FOR_RESTORE_IS_INVALID);
        }
        loginAttemptService.deleteEmailFromCache(email);

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        user.setUserStatus(UserStatus.ACTIVATED);
        userRepo.save(user);
        log.info("User {} unblocked", user.getEmail());
    }

    private User managementCreateNewRegisteredUser(UserManagementDto dto, String refreshTokenKey) {
        return User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(dto.getRole())
            .refreshTokenKey(refreshTokenKey)
            .lastActivityTime(LocalDateTime.now())
            .userStatus(dto.getUserStatus())
            .emailNotification(EmailNotification.DISABLED)
            .rating(AppConstant.DEFAULT_RATING)
            .language(Language.builder()
                .id(2L)
                .code("en")
                .build())
            .build();
    }

    private OwnSecurity managementCreateOwnSecurity(User user) {
        return OwnSecurity.builder()
            .password(passwordEncoder.encode(generatePassword()))
            .user(user)
            .build();
    }

    /**
     * Method that generates random password.
     *
     * @return {@link String} generated password.
     */
    private String generatePassword() {
        SecureRandom secureRandom = new SecureRandom();
        String upperCaseLetters =
            RandomStringUtils.random(2, 0, 27, true, true, VALID_PW_CHARS.toCharArray(), secureRandom);
        String lowerCaseLetters =
            RandomStringUtils.random(2, 27, 53, true, true, VALID_PW_CHARS.toCharArray(), secureRandom);
        String numbers = String.valueOf(secureRandom.nextInt(100));
        String specialChar =
            RandomStringUtils
                .random(2, 0, 0, false, false, "!@#$%^&*()-_=+{}[]|:;<>?,./".toCharArray(), secureRandom);
        String totalChars = RandomStringUtils.random(2, 0, 0, true, true,
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray(), secureRandom);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
            .concat(numbers)
            .concat(specialChar)
            .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    /**
     * Creates and saves password restoration token for a given user and publishes
     * event of sending user approval email.
     *
     * @param user  {@link User} - User whose password is to be recovered
     * @param token {@link String} - token for password restoration
     */
    private User savePasswordRestorationTokenForUser(User user, String token) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(expirationTime))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        user = userRepo.save(user);
        emailService.sendApprovalEmail(user.getId(), user.getName(), user.getEmail(), token);
        return user;
    }

    /**
     * Calculates token expiration date. The amount of hours, after which token will
     * be expired, is set by method parameter.
     *
     * @param expirationTimeInHours - Token expiration delay in hours
     * @return {@link LocalDateTime} - Time at which token will be expired
     */
    private LocalDateTime calculateExpirationDate(Integer expirationTimeInHours) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expirationTimeInHours);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPassword(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return user.getOwnSecurity() != null;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(SetPasswordDto dto, String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        if (hasPassword(email)) {
            throw new UserAlreadyHasPasswordException(ErrorMessage.USER_ALREADY_HAS_PASSWORD);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchesException(ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }
        user.setOwnSecurity(OwnSecurity.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .user(user)
            .build());
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    private void updatePassword(String pass, Long id) {
        String password = passwordEncoder.encode(pass);
        ownSecurityRepo.updatePassword(password, id);
    }

    /**
     * Checks {@code UserStatus} and throws an exception if the user status is
     * DEACTIVATED, BLOCKED, CREATED, or DELETED.
     *
     * @param status - the status of the User
     * @throws BadUserStatusException if the user status is DEACTIVATED, BLOCKED,
     *                                CREATED, or DELETED.
     */
    private void handleUserStatus(UserStatus status) {
        switch (status) {
            case DEACTIVATED:
                throw new BadUserStatusException(ErrorMessage.USER_DEACTIVATED);
            case BLOCKED:
                throw new BadUserStatusException(ErrorMessage.USER_BLOCKED);
            case CREATED:
                throw new BadUserStatusException(ErrorMessage.USER_CREATED);
            case DELETED:
                throw new BadUserStatusException(ErrorMessage.USER_DELETED);
            default:
                break;
        }
    }
}
