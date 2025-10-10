package greencity.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.GreenCityRemoteClient;
import static greencity.constant.AppConstant.*;
import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserInfo;
import greencity.dto.user.UserVO;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.ProjectName;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.IdTokenExpiredException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
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
    private final HttpClient googleAccessTokenVerifier;
    private final ObjectMapper objectMapper;
    private final GreenCityRemoteClient greenCityRemoteClient;

    @Value("${google.resource.userInfoUri}")
    private String userInfoUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(String googleToken, String language, ProjectName projectName) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(googleToken);
            if (googleIdToken == null) {
                throw new IdTokenExpiredException(ErrorMessage.EXPIRED_GOOGLE_ID_TOKEN);
            }
            String email = googleIdToken.getPayload().getEmail();
            String userName = (String) googleIdToken.getPayload().get(USERNAME);
            String profilePicture = (String) googleIdToken.getPayload().get(GOOGLE_PICTURE);
            return processAuthentication(email, userName, profilePicture, language, projectName);
        } catch (IllegalArgumentException e) {
            return authenticateByGoogleAccessToken(googleToken, language, projectName);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + e.getMessage());
        }
    }

    private SuccessSignInDto authenticateByGoogleAccessToken(String googleAccessToken, String language,
        ProjectName projectName) {
        try {
            UserInfo userInfo = getUserInfoFromGoogleAccessToken(googleAccessToken);
            if (userInfo.getEmail() == null) {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
            String email = userInfo.getEmail();
            String userName = userInfo.getName();
            String profilePicture = userInfo.getPicture();
            return processAuthentication(email, userName, profilePicture, language, projectName);
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + e.getMessage());
        }
    }

    private SuccessSignInDto processAuthentication(String email, String userName, String profilePicture,
        String language, ProjectName projectName) {
        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "{}", email);
            return handleNewUser(email, userName, profilePicture, language);
        } else {
            userService.verifyUserStatus(userVO, projectName);
            return getSuccessSignInDto(userVO);
        }
    }

    private SuccessSignInDto handleNewUser(String email, String userName, String profilePicture, String language) {
        UserVO newUser = createNewUser(email, userName, language);
        UserVO savedUser = userService.save(newUser);
        try {
            greenCityRemoteClient.createUbsProfile(modelMapper.map(savedUser, UbsProfileCreationDto.class));
        } catch (RestClientException e) {
            log.error("Failed to create UBS profile for user - {}", savedUser.getEmail(), e);
            throw new RestClientException(ErrorMessage.TRANSACTION_FAILED, e);
        }
        userService.createGreenCityUser(savedUser.getId(), profilePicture);
        UserVO userVO = modelMapper.map(savedUser, UserVO.class);
        log.info("Google sign-up and sign-in user - {}", userVO.getEmail());
        return getSuccessSignInDto(userVO);
    }

    private UserVO createNewUser(String email, String userName, String language) {
        return UserVO.builder()
            .uuid(UUID.randomUUID().toString())
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.VERIFIED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .showLocation(ProfilePrivacyPolicy.PUBLIC)
            .showEcoPlace(ProfilePrivacyPolicy.PUBLIC)
            .showToDoList(ProfilePrivacyPolicy.PUBLIC)
            .languageVO(LanguageVO.builder().id(modelMapper.map(language, Long.class)).build())
            .build();
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
