package greencity.security.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyEmailServiceImpl implements VerifyEmailService {
    private final VerifyEmailRepo verifyEmailRepo;
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Boolean verifyByToken(Long userId, String token) {
        VerifyEmail verifyEmail = verifyEmailRepo
            .findByTokenAndUserId(userId, token)
            .orElseThrow(() -> new BadVerifyEmailTokenException(ErrorMessage.NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));

        if (isNotExpired(verifyEmail.getExpiryDate())) {
            int rows = verifyEmailRepo.deleteVerifyEmailByTokenAndUserId(userId, token);
            log.info("User has successfully verify the email by token {}. Records deleted {}.", token, rows);
            restClient.addUserToSystemChat(userId);
            log.info("The user has been added to the system chats");
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
            UbsProfileCreationDto ubsProfile = modelMapper.map(user, UbsProfileCreationDto.class);
            Long ubsProfileId = restClient.createUbsProfile(ubsProfile);
            log.info("Ubs profile with id {} has been created for user with uuid {}.", ubsProfileId, user.getUuid());
            return true;
        } else {
            log.info("User didn't verify his/her email on time with token {}.", token);
            throw new UserActivationEmailTokenExpiredException(ErrorMessage.EMAIL_TOKEN_EXPIRED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotExpired(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }
}
