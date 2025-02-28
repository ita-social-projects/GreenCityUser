package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.security.UnblockTestersAccountDto;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestersUnblockAccountServiceImpl implements TestersUnblockAccountService {
    private static final String VERIFIED_TOKEN = "m5VZl@example.com";
    private final UserRepo userRepo;

    /**
     * Unblocks user account by provided token and email.
     *
     * @param dto {@link UnblockTestersAccountDto} that contains token and email of
     *            user to be unblocked
     * @throws BadRequestException if token is not valid
     * @throws NotFoundException   if user with given email is not found
     */
    public void unblockAccount(UnblockTestersAccountDto dto) {
        if (!VERIFIED_TOKEN.equalsIgnoreCase(dto.token())) {
            throw new BadRequestException("Invalid token");
        }
        User user = userRepo.findByEmail(dto.userEmail())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        user.setUserStatus(UserStatus.ACTIVATED);
        userRepo.save(user);
    }
}
