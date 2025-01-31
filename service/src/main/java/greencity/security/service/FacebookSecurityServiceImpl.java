package greencity.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.constant.AppConstant;

import static greencity.constant.AppConstant.DEFAULT_RATING;
import static greencity.constant.AppConstant.REGISTRATION_EMAIL_FIELD_NAME;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import greencity.dto.user.UserInfo;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FacebookSecurityServiceImpl implements FacebookSecurityService {
    private final UserService userService;
    private final JwtTool jwtTool;
    private final HttpClient httpClient;
    private final UserRepo userRepo;
    private final PlatformTransactionManager transactionManager;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;


    private static final String NGROK_URL = "https://200a-91-245-77-57.ngrok-free.app";
    @Value("${address}")
    private String address;
    @Value("${spring.social.facebook.app-id}")
    private String facebookAppId;
    @Value("${spring.social.facebook.app-secret}")
    private String facebookAppSecret;
    @Value("https://graph.facebook.com/v18.0/me?fields=id,name,email,picture&access_token=")
    private String userInfoUrl;

    /**
     * {@inheritDoc}
     */
    private FacebookConnectionFactory createFacebookConnection() {
        return new FacebookConnectionFactory(facebookAppId, facebookAppSecret);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link FacebookConnectionFactory}
     */
    @Override
    public String generateFacebookAuthorizeURL() {
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(address + "/facebookSecurity/facebook");
        params.setScope("email");
        return createFacebookConnection()
            .getOAuthOperations()
            .buildAuthenticateUrl(params);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link SuccessSignInDto}
     */
    @Transactional
    @Override
    public SuccessSignInDto generateFacebookAccessToken(String code) {
        String accessToken = createFacebookConnection()
            .getOAuthOperations()
            .exchangeForAccess(code, address + "/facebookSecurity/facebook", null)
            .getAccessToken();
        if (accessToken != null) {
            Facebook facebook = new FacebookTemplate(accessToken);
            UserVO byEmail = facebook.fetchObject(AppConstant.FACEBOOK_OBJECT_ID, UserVO.class, AppConstant.USERNAME,
                REGISTRATION_EMAIL_FIELD_NAME);
            String email = byEmail.getEmail();
            log.info(email);
            String name = byEmail.getName();
            log.info(name);
            byEmail = userService.findByEmail(email);
            UserVO user = byEmail;
            if (user == null) {
                user = modelMapper.map(createNewUser(email, name), UserVO.class);
                log.info("Facebook sign-up and sign-in user - {}", user.getEmail());
            } else {
                log.info("Facebook sign-in exist user - {}", user.getEmail());
            }
            return getSuccessSignInDto(user);
        } else {
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
        }
    }

    private User createNewUser(String email, String userName) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .uuid(UUID.randomUUID().toString())
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .build();
    }

    public SuccessSignInDto authenticate(String fbToken, String language) {
        try {
            UserInfo userInfo = getUserInfoFromFacebook(fbToken);
            if (userInfo.getEmail() == null) {
                throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
            }
            String profilePicture = null;
            if (userInfo.getPicture() != null) {
                profilePicture = userInfo.getPicture();
            }
            return processAuthentication(userInfo.getEmail(), userInfo.getName(), profilePicture, language);
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN + e.getMessage());
        }
    }

    private SuccessSignInDto processAuthentication(String email, String userName, String profilePicture, String language) {
        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "{}", email);
            return handleNewUser(email, userName, profilePicture, language);
        } else {
            if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
            }
            log.info("Facebook sign-in exist user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        }
    }

    private SuccessSignInDto handleNewUser(String email, String userName, String profilePicture, String language) {
        User newUser = createNewUser(email, userName, profilePicture, language);
        User savedUser = saveNewUser(newUser);
        try {
            restClient.createUbsProfile(modelMapper.map(savedUser, UbsProfileCreationDto.class));
        } catch (RestClientException e) {
            log.error("Failed to create UBS profile for user - {}", savedUser.getEmail(), e);
            throw new RestClientException(ErrorMessage.TRANSACTION_FAILED, e);
        }
        UserVO userVO = modelMapper.map(savedUser, UserVO.class);
        log.info("Facebook sign-up and sign-in user - {}", userVO.getEmail());
        return getSuccessSignInDto(userVO);
    }

    private User createNewUser(String email, String userName, String profilePicture, String language) {
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
                .rating(DEFAULT_RATING)
                .language(Language.builder().id(modelMapper.map(language, Long.class)).build())
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

    private User saveNewUser(User newUser) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            newUser.setUuid(UUID.randomUUID().toString());
            Long id = userRepo.save(newUser).getId();
            newUser.setId(id);
            return newUser;
        });
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }

    private UserInfo getUserInfoFromFacebook(String accessToken) throws IOException {
        String requestUrl = userInfoUrl + "?fields=id,name,email,picture&access_token=" + accessToken;
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = httpClient.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        String id = jsonNode.get("id").asText();
        String name = jsonNode.get("name").asText();
        String email = jsonNode.get("email").asText();
        String pictureUrl = null;

        if (jsonNode.has("picture")) {
            JsonNode pictureNode = jsonNode.get("picture").get("data");
            if (pictureNode != null && pictureNode.has("url")) {
                pictureUrl = pictureNode.get("url").asText();
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setName(name);
        userInfo.setEmail(email);
        userInfo.setPicture(pictureUrl);

        return userInfo;
    }
}
