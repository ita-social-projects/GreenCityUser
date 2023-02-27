package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
    private final RestClient restClient;
    private final GoogleSecurityTransactionalService googleSecurityTransactionalService;

    /**
     * Constructor.
     *
     * @param userService                        {@link UserService} - service of
     *                                           {@link User} logic.
     * @param jwtTool                            {@link JwtTool} - tool for jwt
     *                                           logic.
     * @param googleIdTokenVerifier              {@link GoogleIdTokenVerifier} -
     *                                           tool for verify.
     * @param modelMapper                        {@link ModelMapper} - tool for
     *                                           mapping models.
     * @param restClient                         {@link RestClient} - tool for
     *                                           sending requests
     * @param googleSecurityTransactionalService {@link GoogleSecurityTransactionalService}
     *                                           - service with transactional
     *                                           methods
     */
    @Autowired
    public GoogleSecurityServiceImpl(UserService userService,
        JwtTool jwtTool,
        GoogleIdTokenVerifier googleIdTokenVerifier,
        ModelMapper modelMapper,
        RestClient restClient,
        GoogleSecurityTransactionalService googleSecurityTransactionalService) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
        this.googleSecurityTransactionalService = googleSecurityTransactionalService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(String idToken, String language) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();
                UserVO userVO = userService.findByEmail(email);
                if (userVO == null) {
                    log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
                    User user = googleSecurityTransactionalService.signUp(payload, language);
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
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}
