package greencity.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import greencity.enums.*;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class FacebookSecurityServiceImpl implements FacebookSecurityService {
    private final UserService userService;
    private final JwtTool jwtTool;
    private final HttpClient httpClient;
    private final UserRepo userRepo;
    private final PlatformTransactionManager transactionManager;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${address}")
    private String address;
    @Value("${spring.social.facebook.app-id}")
    private String facebookAppId;
    @Value("${spring.social.facebook.app-secret}")
    private String facebookAppSecret;
    @Value("${facebook.resource.userInfoUri}")
    private String userInfoUrl;

    public FacebookSecurityServiceImpl(
            UserService userService,
            JwtTool jwtTool,
            HttpClient httpClient,
            UserRepo userRepo,
            PlatformTransactionManager transactionManager,
            ModelMapper modelMapper,
            RestClient restClient,
            ObjectMapper objectMapper,
            @Qualifier("facebookWebClient") WebClient webClient
    ) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.httpClient = httpClient;
        this.userRepo = userRepo;
        this.transactionManager = transactionManager;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }

    @Override
    public String generateFacebookAuthorizeURL() {
        return "https://www.facebook.com/v22.0/dialog/oauth"
            + "?client_id=" + facebookAppId
            + "&redirect_uri=" + address + "/facebookSecurity/facebook"
            + "&scope=email";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link SuccessSignInDto}
     */
    @Transactional
    @Override
    public SuccessSignInDto generateFacebookAccessToken(String code) {
        log.info("Starting retrieval of Facebook Access Token for code: {}", code);

        String tokenUrl = "https://graph.facebook.com/v19.0/oauth/access_token"
            + "?client_id=" + facebookAppId
            + "&redirect_uri=" + address + "/facebookSecurity/facebook"
            + "&client_secret=" + facebookAppSecret
            + "&code=" + code;

        String accessToken = webClient.get()
            .uri(tokenUrl)
            .retrieve()
            .onStatus(status -> status != HttpStatus.OK,
                response -> Mono.error(new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN)))
            .bodyToMono(String.class)
            .map(response -> {
                try {
                    JsonNode jsonNode = objectMapper.readTree(response);
                    return jsonNode.get("access_token").asText();
                } catch (Exception e) {
                    throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN, e);
                }
            })
            .doOnSuccess(token -> log.info("Successfully retrieved Facebook Access Token: {}", token))
            .doOnError(e -> log.error("Error retrieving Access Token: {}", e.getMessage()))
            .block();

        if (accessToken != null) {
            String userInfoRequestUrl = userInfoUrl + "?fields=id,name,email&access_token=" + accessToken;
            log.info(" Executing request to Facebook API: {}", userInfoRequestUrl);

            UserVO byEmail = webClient.get()
                .uri(userInfoRequestUrl)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK,
                    response -> Mono.error(new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN)))
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
                        String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "Unknown";
                        log.info(" Received email: {}, name: {}", email, name);
                        return processUser(email, name);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN, e);
                    }
                })
                .doOnError(e -> log.error(" Error retrieving user data: {}", e.getMessage()))
                .block();

            return getSuccessSignInDto(byEmail);
        } else {
            log.error(" Access Token == null");
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
        }
    }

    private UserVO processUser(String email, String name) {
        UserVO byEmail = userService.findByEmail(email);
        if (byEmail == null) {
            log.info("User with email {} not found. Creating a new one.", email);
            User newUser = createNewUser(email, name);
            User savedUser = saveNewUser(newUser);
            byEmail = modelMapper.map(savedUser, UserVO.class);
            log.info("Created new user with ID: {}", byEmail.getId());
        } else {
            log.info("User {} found in database.", email);
        }
        return byEmail;
    }

    User createNewUser(String email, String name) {
        return User.builder()
            .email(email)
            .name(name)
            .role(Role.ROLE_USER)
            .uuid(UUID.randomUUID().toString())
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .languageId(1L)
            .build();
    }

    User createNewUser(String email, String userName, String profilePicture, String language) {
        User user = User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .profilePicturePath(profilePicture)
            .showLocation(ProfilePrivacyPolicy.PUBLIC)
            .showEcoPlace(ProfilePrivacyPolicy.PUBLIC)
            .showToDoList(ProfilePrivacyPolicy.PUBLIC)
            .languageId(modelMapper.map(language, Long.class))
            .build();

        Set<UserNotificationPreference> userNotificationPreferences = Arrays.stream(EmailPreference.values())
            .map(emailPreference -> UserNotificationPreference.builder()
                .user(user)
                .emailPreference(emailPreference)
                .periodicity(EmailPreferencePeriodicity.TWICE_A_DAY)
                .build())
            .collect(Collectors.toSet());
        user.setNotificationPreferences(userNotificationPreferences);
        return user;
    }

    User saveNewUser(User newUser) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            newUser.setUuid(UUID.randomUUID().toString());
            Long id = userRepo.save(newUser).getId();
            newUser.setId(id);
            log.info("User saved with ID: {}", id);
            return newUser;
        });
    }

    @Override
    public SuccessSignInDto authenticate(String fbToken, String language) {
        if (fbToken == null || language == null) {
            throw new IllegalArgumentException(ErrorMessage.FB_TOKEN_OR_LANGUAGE_MISSING);
        }

        UserInfo userInfo = webClient.get()
            .uri(userInfoUrl + "?fields=id,name,email,picture&access_token=" + fbToken)
            .retrieve()
            .onStatus(status -> status != HttpStatus.OK,
                response -> Mono.error(new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN)))
            .bodyToMono(UserInfo.class)
            .doOnSuccess(info -> log.info("Received UserInfo: email={}, name={}", info.getEmail(), info.getName()))
            .doOnError(e -> log.error("Error retrieving data: {}", e.getMessage()))
            .block();

        if (userInfo == null) {
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
        }

        if (userInfo.getEmail() == null) {
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
        }

        return processAuthentication(userInfo.getEmail(), userInfo.getName(), userInfo.getPicture(), language);
    }

    SuccessSignInDto processAuthentication(String email, String userName, String profilePicture, String language) {
        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "{}", email);
            return handleNewUser(email, userName, profilePicture, language);
        } else {
            if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
            }
            return getSuccessSignInDto(userVO);
        }
    }

    SuccessSignInDto handleNewUser(String email, String userName, String profilePicture, String language) {
        User newUser = createNewUser(email, userName, profilePicture, language);
        User savedUser = saveNewUser(newUser);
        try {
            restClient.createUbsProfile(modelMapper.map(savedUser, UbsProfileCreationDto.class));
        } catch (RestClientException e) {
            throw new RestClientException(ErrorMessage.TRANSACTION_FAILED, e);
        }
        UserVO userVO = modelMapper.map(savedUser, UserVO.class);
        return getSuccessSignInDto(userVO);
    }

    SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}
