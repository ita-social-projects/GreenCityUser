package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.entity.OwnSecurity;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.UserRepo;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.events.UpdatePasswordEvent;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.EmailService;
import jakarta.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for password recovery functionality. It manages recovery tokens
 * creation and persistence as well as minimal validation, but neither updates
 * the password directly, nor sends a recovery email. These parts of the
 * recovery process are done by separate event listeners.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private final OwnSecurityRepo ownSecurityRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JwtTool jwtTool;
    private final EmailService emailService;
    @Value("${verifyEmailTimeHour}")
    private Integer tokenExpirationTimeInHours;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void sendPasswordRecoveryEmailTo(String email, boolean isUbs) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        RestorePasswordEmail restorePasswordEmail = user.getRestorePasswordEmail();
        if (restorePasswordEmail != null) {
            throw new WrongEmailException(ErrorMessage.PASSWORD_RESTORE_LINK_ALREADY_SENT + email);
        }
        savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKeyWithCodedDate(), isUbs);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void updatePasswordUsingToken(OwnRestoreDto form) {
        RestorePasswordEmail restorePasswordEmail = restorePasswordEmailRepo.findByToken(form.getToken())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.LINK_IS_NO_ACTIVE));
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BadRequestException(ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }
        User user = restorePasswordEmail.getUser();
        UserStatus userStatus = restorePasswordEmail.getUser().getUserStatus();
        if (isNotExpired(restorePasswordEmail.getExpiryDate())) {
            updatePassword(form.getPassword(), restorePasswordEmail.getUser().getId());
            emailService.sendSuccessRestorePasswordByEmail(user.getEmail(), user.getLanguage().getCode(),
                user.getName(), form.getIsUbs());
            applicationEventPublisher.publishEvent(
                new UpdatePasswordEvent(this, form.getPassword(), restorePasswordEmail.getUser().getId()));
            user.setRestorePasswordEmail(null);
            restorePasswordEmailRepo.delete(restorePasswordEmail);
            log.info("User with email {} has successfully restored the password using token {}",
                restorePasswordEmail.getUser().getEmail(), form.getToken());
        } else {
            log.info("Password restoration token of user with email {} has been expired. Token: {}",
                restorePasswordEmail.getUser().getEmail(), form.getToken());
            throw new UserActivationEmailTokenExpiredException(ErrorMessage.LINK_IS_NO_ACTIVE);
        }
        if (userStatus == UserStatus.CREATED) {
            restorePasswordEmail.getUser().setUserStatus(UserStatus.ACTIVATED);
        }
    }

    /**
     * Creates and saves password restoration token for a given user and publishes
     * event of sending password recovery email to the user.
     *
     * @param user  {@link User} - User whose password is to be recovered
     * @param token {@link String} - token for password restoration
     */
    private void savePasswordRestorationTokenForUser(User user, String token, boolean isUbs) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(tokenExpirationTimeInHours))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        emailService.sendRestoreEmail(
            user.getId(),
            user.getFirstName(),
            user.getEmail(),
            token,
            user.getLanguage().getCode(),
            isUbs);
    }

    /**
     * Checks if the given date is before current {@link LocalDateTime}. Use this
     * method for checking for token expiration.
     *
     * @param tokenExpirationDate - Token expiration date
     * @return {@code boolean} - Whether token is expired or not
     */
    private boolean isNotExpired(LocalDateTime tokenExpirationDate) {
        return LocalDateTime.now().isBefore(tokenExpirationDate);
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

    /**
     * Removes all the expired tokens from the database each period of time.
     * Interval is set by @Scheduled annotation. Access modifier is set to
     * package-private since this method should be invoked by Spring Framework only.
     */
    @Scheduled(fixedRate = 24, timeUnit = TimeUnit.HOURS)
    void deleteAllExpiredPasswordResetTokens() {
        int rows = restorePasswordEmailRepo.deleteAllExpiredPasswordResetTokens();
        log.info("{} password reset tokens were deleted.", rows);
    }

    private void updatePassword(String pass, Long id) {
        String password = passwordEncoder.encode(pass);
        Optional<OwnSecurity> ownSecurity = ownSecurityRepo.findByUserId(id);

        ownSecurity.ifPresentOrElse(s -> {
            s.setPassword(password);
            ownSecurityRepo.save(s);
        }, () -> ownSecurityRepo.save(createOwnSecurity(id, password)));
    }

    private OwnSecurity createOwnSecurity(Long id, String password) {
        return OwnSecurity.builder()
            .password(password)
            .user(userRepo.findById(id).orElseThrow(NoResultException::new))
            .build();
    }
}
