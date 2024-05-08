package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static greencity.security.service.OwnSecurityServiceImpl.getUserAchievements;
import static greencity.security.service.OwnSecurityServiceImpl.getUserActions;
import greencity.service.AchievementService;
import greencity.service.UserService;
import java.io.IOException;
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
     * @param httpClient            {@link HttpClient} - ...
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
        HttpClient httpClient) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.modelMapper = modelMapper;
        this.achievementService = achievementService;
        this.userRepo = userRepo;
        this.restClient = restClient;
        this.transactionManager = transactionManager;
        this.httpClient = httpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(String accessToken, String language) {
        try {
            // GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            UserInfo userInfo = getUserCredentials(accessToken);
            if (userInfo != null) {
                // GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String email = userInfo.getEmail();
                String userName = userInfo.getName();
                UserVO userVO = userService.findByEmail(email);
                if (userVO == null) {
                    log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
                    String profilePicture = userInfo.getPicture();
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    User user = transactionTemplate.execute(status -> {
                        User savedUser = createNewUser(email, userName, profilePicture, language);
                        savedUser.setUserAchievements(createUserAchievements(savedUser));
                        savedUser.setUserActions(createUserActions(savedUser));
                        savedUser.setUuid(UUID.randomUUID().toString());
                        savedUser.setFirstName(userName);
                        Long id = userRepo.save(savedUser).getId();
                        savedUser.setId(id);
                        return savedUser;
                    });
                    restClient.createUbsProfile(modelMapper.map(user, UbsProfileCreationDto.class));
                    userVO = modelMapper.map(user, UserVO.class);
                    log.info("Google sign-up and sign-in user - {}", userVO.getEmail());
                    return getSuccessSignInDto(userVO);
                } else {
                    if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                        throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
                    }
                    log.info("Google sign-in exist user - {}", userVO.getEmail());
                    return getSuccessSignInDto(userVO);
                }
            } else {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private User createNewUser(String email, String userName, String profilePicture,
        String language) {
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
            .language(Language.builder()
                .id(modelMapper.map(language, Long.class))
                .build())
            .build();
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

    private UserInfo getUserCredentials(String accessToken) throws IOException {
        String requestUrl = userInfoUrl + accessToken;
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = httpClient.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, UserInfo.class);
    }
}
