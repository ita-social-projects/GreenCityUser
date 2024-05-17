package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.RestClient;
import static greencity.constant.AppConstant.DEFAULT_RATING;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.UserAction;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import static greencity.constant.AppConstant.GOOGLE_PICTURE;
import static greencity.constant.AppConstant.USERNAME;
import static greencity.security.service.OwnSecurityServiceImpl.getUserAchievements;
import static greencity.security.service.OwnSecurityServiceImpl.getUserActions;
import greencity.service.AchievementService;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class GoogleSecurityServiceImpl implements GoogleSecurityService {
    private final UserService userService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtTool jwtTool;
    private final ModelMapper modelMapper;
    private final AchievementService achievementService;
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final PlatformTransactionManager transactionManager;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${google.resource.userInfoUri}")
    private String userInfoUrl;

    /**
     * Constructor.
     *
     * @param userService           {@link UserService} - service of {@link User}
     *                              logic.
     * @param jwtTool               {@link JwtTool} - tool for jwt logic.
     * @param googleIdTokenVerifier {@link GoogleIdTokenVerifier} - tool for verify.
     * @param modelMapper           {@link ModelMapper} - tool for mapping models.
     * @param restClient            {@link RestClient} - tool for sending requests
     * @param transactionManager    {@link PlatformTransactionManager} - tool for
     *                              transaction management
     * @param httpClient            {@link HttpClient} - client for HTTP request
     *                              execution.
     * @param objectMapper          {@link ObjectMapper} - object mapper.
     */
    @Autowired
    public GoogleSecurityServiceImpl(UserService userService,
        JwtTool jwtTool,
        GoogleIdTokenVerifier googleIdTokenVerifier,
        ModelMapper modelMapper,
        AchievementService achievementService,
        UserRepo userRepo,
        RestClient restClient,
        PlatformTransactionManager transactionManager,
        HttpClient httpClient,
        ObjectMapper objectMapper) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.modelMapper = modelMapper;
        this.achievementService = achievementService;
        this.userRepo = userRepo;
        this.restClient = restClient;
        this.transactionManager = transactionManager;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(String token, String language) {
        try {
            return authenticateByTokenId(token, language);
        } catch (IllegalArgumentException e) {
            return authenticateByAccessToken(token, language);
        }
    }

    private SuccessSignInDto authenticateByTokenId(String tokenId, String language) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(tokenId);
            if (googleIdToken == null) {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
            return processAuthentication(googleIdToken, language);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private SuccessSignInDto authenticateByAccessToken(String accessToken, String language) {
        try {
            UserInfo userInfo = verifyAccessToken(accessToken);
            if (userInfo.getEmail() == null) {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
            return processAuthentication(userInfo, language);
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private SuccessSignInDto processAuthentication(Object credentials, String language) {
        String email = getEmailFromCredentials(credentials);
        String userName = getUserNameFromCredentials(credentials);

        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
            return handleNewUser(credentials, email, userName, language);
        } else {
            if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
            }
            log.info("Google sign-in exist user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        }
    }

    private String getEmailFromCredentials(Object credentials) {
        return (credentials instanceof GoogleIdToken)
            ? ((GoogleIdToken) credentials).getPayload().getEmail()
            : ((UserInfo) credentials).getEmail();
    }

    private String getUserNameFromCredentials(Object credentials) {
        return (credentials instanceof GoogleIdToken)
            ? (String) ((GoogleIdToken) credentials).getPayload().get(USERNAME)
            : ((UserInfo) credentials).getName();
    }

    private SuccessSignInDto handleNewUser(Object credentials, String email, String userName, String language) {
        String profilePicture = null;
        if (credentials instanceof GoogleIdToken) {
            GoogleIdToken.Payload payload = ((GoogleIdToken) credentials).getPayload();
            profilePicture = (String) payload.get(GOOGLE_PICTURE);
        } else if (credentials instanceof UserInfo) {
            profilePicture = ((UserInfo) credentials).getPicture();
        }
        User newUser = createNewUser(email, userName, profilePicture, language);

        User savedUser = saveNewUserWithTransactions(newUser);
        restClient.createUbsProfile(modelMapper.map(savedUser, UbsProfileCreationDto.class));

        UserVO userVO = modelMapper.map(savedUser, UserVO.class);
        log.info("Google sign-up and sign-in user - {}", userVO.getEmail());
        return getSuccessSignInDto(userVO);
    }

    private User createNewUser(String email, String userName, String profilePicture, String language) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .profilePicturePath(profilePicture)
            .rating(DEFAULT_RATING)
            .language(Language.builder().id(modelMapper.map(language, Long.class)).build())
            .build();
    }

    private User saveNewUserWithTransactions(User newUser) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            newUser.setUserAchievements(createUserAchievements(newUser));
            newUser.setUserActions(createUserActions(newUser));
            newUser.setUuid(UUID.randomUUID().toString());
            Long id = userRepo.save(newUser).getId();
            newUser.setId(id);
            return newUser;
        });
    }

    private List<UserAchievement> createUserAchievements(User user) {
        return getUserAchievements(user, achievementService);
    }

    private List<UserAction> createUserActions(User user) {
        return getUserActions(user, achievementService);
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }

    private UserInfo verifyAccessToken(String accessToken) throws IOException {
        String requestUrl = userInfoUrl + accessToken;
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = httpClient.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        return objectMapper.readValue(jsonResponse, UserInfo.class);
    }
}
