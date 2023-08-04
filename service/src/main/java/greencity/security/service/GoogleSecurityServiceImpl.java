package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static greencity.constant.AppConstant.*;

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
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final PlatformTransactionManager transactionManager;

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
     */
    @Autowired
    public GoogleSecurityServiceImpl(UserService userService,
        JwtTool jwtTool,
        GoogleIdTokenVerifier googleIdTokenVerifier,
        ModelMapper modelMapper,
        UserRepo userRepo,
        RestClient restClient,
        PlatformTransactionManager transactionManager) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.restClient = restClient;
        this.transactionManager = transactionManager;
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
                String userName = (String) payload.get(USERNAME);
                UserVO userVO = userService.findByEmail(email);
                if (userVO == null) {
                    log.info(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
                    String profilePicture = (String) payload.get(GOOGLE_PICTURE);
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    User user = transactionTemplate.execute(status -> {
                        User savedUser = createNewUser(email, userName, profilePicture, language);
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
        } catch (GeneralSecurityException | IOException e) {
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

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}
