package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.UserAction;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static greencity.constant.AppConstant.*;
import static greencity.security.service.OwnSecurityServiceImpl.getUserAchievements;
import static greencity.security.service.OwnSecurityServiceImpl.getUserActions;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleSecurityTransactionalServiceImpl implements GoogleSecurityTransactionalService {
    private final JwtTool jwtTool;
    private final ModelMapper modelMapper;
    private final AchievementService achievementService;
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Transactional
    public User signUp(GoogleIdToken.Payload payload, String language) {
        String email = payload.getEmail();
        String userName = (String) payload.get(USERNAME);
        String profilePicture = (String) payload.get(GOOGLE_PICTURE);
        User user = createNewUser(email, userName, profilePicture, language);
        user.setUserAchievements(createUserAchievements(user));
        user.setUserActions(createUserActions(user));
        user.setUuid(UUID.randomUUID().toString());
        User savedUser = userRepo.save(user);
        user.setId(savedUser.getId());
        return user;
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
}
