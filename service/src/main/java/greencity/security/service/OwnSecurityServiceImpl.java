package greencity.security.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRefreshTokenException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.PasswordsDoNotMatchesException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.repository.UserRepo;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.AchievementService;
import greencity.service.EmailService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class OwnSecurityServiceImpl implements OwnSecurityService {
    private final OwnSecurityRepo ownSecurityRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final Integer expirationTime;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private static final String VALID_PW_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>?,./";
    private final AchievementService achievementService;
    private final EmailService emailService;

    /**
     * Constructor.
     */
    @Autowired
    public OwnSecurityServiceImpl(OwnSecurityRepo ownSecurityRepo,
        UserService userService,
        PasswordEncoder passwordEncoder,
        JwtTool jwtTool,
        @Value("${verifyEmailTimeHour}") Integer expirationTime,
        RestorePasswordEmailRepo restorePasswordEmailRepo,
        ModelMapper modelMapper,
        UserRepo userRepo,
        AchievementService achievementService, EmailService emailService, RestClient restClient) {
        this.ownSecurityRepo = ownSecurityRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTool = jwtTool;
        this.expirationTime = expirationTime;
        this.restorePasswordEmailRepo = restorePasswordEmailRepo;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.achievementService = achievementService;
        this.emailService = emailService;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Transactional
    @Override
    public SuccessSignUpDto signUp(OwnSignUpDto dto, String language) {
        User user = createNewRegisteredUser(dto, jwtTool.generateTokenKey(), language);
        OwnSecurity ownSecurity = createOwnSecurity(dto, user);
        VerifyEmail verifyEmail = createVerifyEmail(user, jwtTool.generateTokenKey());
        List<UserAchievement> userAchievementList = createUserAchievements(user);
        List<UserAction> userActionsList = createUserActions(user);
        user.setOwnSecurity(ownSecurity);
        user.setVerifyEmail(verifyEmail);
        user.setUserAchievements(userAchievementList);
        user.setUserActions(userActionsList);
        user.setUuid(UUID.randomUUID().toString());
        try {
            User savedUser = userRepo.save(user);
            user.setId(savedUser.getId());
            /*
             * achievementService.findUserAchievement(savedUser.getId(),
             * DEFAULT_ACHIEVEMENT_ID);
             */
            emailService.sendVerificationEmail(savedUser.getId(), savedUser.getName(), savedUser.getEmail(),
                savedUser.getVerifyEmail().getToken(), language);
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
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .rating(AppConstant.DEFAULT_RATING)
            .language(Language.builder()
                .id(modelMapper.map(language, Long.class))
                .build())
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
            .expiryDate(calculateExpirationDateTime())
            .build();
    }

    private List<UserAchievement> createUserAchievements(User user) {
        return getUserAchievements(user, achievementService);
    }

    private List<UserAction> createUserActions(User user) {
        return getUserActions(user, achievementService);
    }

    static List<UserAchievement> getUserAchievements(User user, AchievementService achievementService) {
        List<Achievement> achievementList = buildAchievementList(achievementService.findAll());
        return achievementList.stream()
            .map(a -> {
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setAchievement(a);
                userAchievement.setUser(user);
                return userAchievement;
            })
            .collect(Collectors.toList());
    }

    static List<UserAction> getUserActions(User user, AchievementService achievementService) {
        List<Achievement> achievementList = buildAchievementList(achievementService.findAll());
        return achievementList.stream()
            .map(a -> {
                UserAction userAction = new UserAction();
                userAction.setAchievementCategory(a.getAchievementCategory());
                userAction.setUser(user);
                return userAction;
            })
            .collect(Collectors.toList());
    }

    static List<Achievement> buildAchievementList(List<AchievementVO> achievementVOList) {
        List<Achievement> achievements = new ArrayList<>();
        for (AchievementVO achievementVO : achievementVOList) {
            achievements.add(Achievement.builder()
                .id(achievementVO.getId())
                .achievementCategory(AchievementCategory.builder()
                    .id(achievementVO.getAchievementCategory().getId())
                    .name(achievementVO.getAchievementCategory().getName())
                    .build())
                .build());
        }
        return achievements;
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
        UserVO user = userService.findByEmail(dto.getEmail());
        if (user == null) {
            throw new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail());
        }
        if (!isPasswordCorrect(dto, user)) {
            throw new WrongPasswordException(ErrorMessage.BAD_PASSWORD);
        }
        if (user.getVerifyEmail() != null) {
            throw new EmailNotVerified("You should verify the email first, check your email box!");
        }
        if (user.getUserStatus() == UserStatus.DEACTIVATED) {
            throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
        }
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), true);
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
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void updatePassword(String pass, Long id) {
        String password = passwordEncoder.encode(pass);
        ownSecurityRepo.updatePassword(password, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateCurrentPassword(UpdatePasswordDto updatePasswordDto, String email) {
        UserVO user = userService.findByEmail(email);
        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchesException(ErrorMessage.PASSWORDS_DO_NOT_MATCHES);
        }
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getOwnSecurity().getPassword())) {
            throw new PasswordsDoNotMatchesException(ErrorMessage.PASSWORD_DOES_NOT_MATCH);
        }
        updatePassword(updatePasswordDto.getPassword(), user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void managementRegisterUser(UserManagementDto dto) {
        User user = managementCreateNewRegisteredUser(dto, jwtTool.generateTokenKey());
        OwnSecurity ownSecurity = managementCreateOwnSecurity(user);
        user.setOwnSecurity(ownSecurity);
        savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey());
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
    private void savePasswordRestorationTokenForUser(User user, String token) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(expirationTime))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        userService.save(modelMapper.map(user, UserVO.class));
        emailService.sendApprovalEmail(user.getId(), user.getName(), user.getEmail(), token);
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
}
