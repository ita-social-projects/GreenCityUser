package greencity.security.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code VerifyEmailService}.
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VerifyEmailServiceImpl implements VerifyEmailService {
    private final VerifyEmailRepo verifyEmailRepo;
    private final UserRepo userRepo;
    private final OwnSecurityService ownSecurityService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean verifyByToken(Long userId, String token) {
        VerifyEmail verifyEmail = verifyEmailRepo.findByTokenAndUserId(token, userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_EXPIRED));
        User user = verifyEmail.getUser();

        ownSecurityService.createExternalUserProfiles(user.getId());
        user.setUserStatus(UserStatus.VERIFIED);
        userRepo.save(user);
        verifyEmailRepo.deleteByTokenAndUserId(token, userId);

        log.info("User has successfully verified the email by token {}.", token);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(cron = "0 0 0 * * *", zone = AppConstant.UKRAINE_TIMEZONE)
    public void removeUnusedTokensWithAccounts() {
        List<User> usersToDelete = verifyEmailRepo.findAll().stream()
            .map(VerifyEmail::getUser)
            .toList();
        userRepo.deleteAll(usersToDelete);
    }
}
