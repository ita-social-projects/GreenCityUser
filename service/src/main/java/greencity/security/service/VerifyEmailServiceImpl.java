package greencity.security.service;

import greencity.client.GreenCityRemoteClient;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    private final RestClient restClient;
    private final ModelMapper modelMapper;
    private final GreenCityRemoteClient greenCityRemoteClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean verifyByToken(Long userId, String token) {
        VerifyEmail verifyEmail = verifyEmailRepo.findByTokenAndUserId(token, userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_EXPIRED));

        User user = verifyEmail.getUser();
        try {
            Long ubsProfileId = restClient.createUbsProfile(modelMapper.map(user, UbsProfileCreationDto.class));
            log.info("Ubs profile with id {} has been created for user with uuid {}.", ubsProfileId, user.getUuid());
        } catch (RestClientException e) {
            log.warn("Ubs profile has not been created for user with uuid {}.", user.getUuid());
            return false;
        }

        user.setUserStatus(UserStatus.ACTIVATED);
        user = userRepo.save(user);

        try {
            greenCityRemoteClient.createUser(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build());
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        } catch (WebClientRequestException | WebClientResponseException e) {
            log.warn("GreenCity service is unavailable: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected error when calling GreenCity: {}", e.getMessage(), e);
        }

        verifyEmailRepo.deleteByTokenAndUserId(token, userId);
        log.info("User has successfully verify the email by token {}.", token);

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
