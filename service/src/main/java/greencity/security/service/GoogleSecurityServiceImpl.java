package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.RestClient;
import static greencity.constant.AppConstant.*;
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
import greencity.exception.exceptions.IdTokenExpiredException;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import static greencity.security.service.OwnSecurityServiceImpl.getUserAchievements;
import static greencity.security.service.OwnSecurityServiceImpl.getUserActions;
import greencity.service.AchievementService;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSecurityServiceImpl implements GoogleSecurityService {
    private final UserService userService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtTool jwtTool;
    private final ModelMapper modelMapper;
    private final AchievementService achievementService;
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final PlatformTransactionManager transactionManager;
    private final HttpClient googleAccessTokenVerifier;
    private final ObjectMapper objectMapper;

    @Value("${google.resource.userInfoUri}")
    private String userInfoUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(String googleToken, String language) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(googleToken);
            if (googleIdToken == null) {
                throw new IdTokenExpiredException(ErrorMessage.EXPIRED_GOOGLE_ID_TOKEN);
            }
            String email = googleIdToken.getPayload().getEmail();
            String userName = (String) googleIdToken.getPayload().get(USERNAME);
            String profilePicture = (String) googleIdToken.getPayload().get(GOOGLE_PICTURE);
            return processAuthentication(email, userName, profilePicture, language);
        } catch (IllegalArgumentException e) {
            return authenticateByGoogleAccessToken(googleToken, language);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + e.getMessage());
        }
    }

    private SuccessSignInDto authenticateByGoogleAccessToken(String googleAccessToken, String language) {
        try {
            UserInfo userInfo = getUserInfoFromGoogleAccessToken(googleAccessToken);
            if (userInfo.getEmail() == null) {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
            String email = userInfo.getEmail();
            String userName = userInfo.getName();
            String profilePicture = userInfo.getPicture();
            return processAuthentication(email, userName, profilePicture, language);
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + e.getMessage());
        }
    }

    private SuccessSignInDto processAuthentication(String email, String userName, String profilePicture,
        String language) {
        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "{}", email);
            return handleNewUser(email, userName, profilePicture, language);
        } else {
            if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
            }
            log.info("Google sign-in exist user - {}", userVO.getEmail());
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

    private User saveNewUser(User newUser) {
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

    private UserInfo getUserInfoFromGoogleAccessToken(String accessToken) throws IOException {
        String requestUrl = userInfoUrl + accessToken;
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = googleAccessTokenVerifier.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        return objectMapper.readValue(jsonResponse, UserInfo.class);
    }
}
