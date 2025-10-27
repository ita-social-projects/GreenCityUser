package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserProfileCreationException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceImplTest {
    @Mock
    VerifyEmailRepo verifyEmailRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    OwnSecurityService ownSecurityService;

    User user = User.builder()
        .id(1L)
        .userStatus(UserStatus.CREATED)
        .name("user")
        .build();

    VerifyEmail verifyEmail = VerifyEmail.builder()
        .id(1L)
        .token("token")
        .user(user)
        .build();

    @InjectMocks
    VerifyEmailServiceImpl verifyEmailService;

    @Test
    void verifyByTokenNotExpiredTokenTest() {
        when(verifyEmailRepo.findByTokenAndUserId("token", 1L)).thenReturn(Optional.of(verifyEmail));
        when(userRepo.save(any(User.class))).thenReturn(user);

        verifyEmailService.verifyByToken(1L, "token");

        verify(verifyEmailRepo).deleteByTokenAndUserId("token", 1L);
        verify(ownSecurityService).createExternalUserProfiles(user.getId());
    }

    @Test
    void verifyWhenTokenNotExpiredAndRestClientThrowsExceptionTest() {
        String exceptionMessage = "exception message";
        String token = "token";
        Long userId = 1L;
        User mockUser = mock(User.class);
        VerifyEmail mockVerifyEmail = mock(VerifyEmail.class);

        when(verifyEmailRepo.findByTokenAndUserId(token, userId)).thenReturn(Optional.of(mockVerifyEmail));
        when(mockVerifyEmail.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(userId);
        doThrow(new UserProfileCreationException(exceptionMessage))
            .when(ownSecurityService).createExternalUserProfiles(userId);

        assertThrows(UserProfileCreationException.class,
            () -> verifyEmailService.verifyByToken(userId, "token"));

        verify(ownSecurityService).createExternalUserProfiles(userId);
        verify(mockUser, never()).setUserStatus(UserStatus.VERIFIED);
        verify(userRepo, never()).save(any(User.class));
        verify(verifyEmailRepo, never()).deleteByTokenAndUserId(token, userId);
    }

    @Test
    void verifyByTokenNoTokenFoundTest() {
        String expectedExceptionMessage = ErrorMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_EXPIRED;

        when(verifyEmailRepo.findByTokenAndUserId("token", 1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> verifyEmailService.verifyByToken(1L, "token"));

        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void removeUnusedTokensWithAccountsTest() {
        when(verifyEmailRepo.findAll()).thenReturn(List.of(verifyEmail));

        verifyEmailService.removeUnusedTokensWithAccounts();

        verify(userRepo).deleteAll(List.of(user));
    }
}
