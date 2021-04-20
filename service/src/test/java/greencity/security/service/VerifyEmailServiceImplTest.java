package greencity.security.service;

import greencity.client.RestClient;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceImplTest {
    @Mock
    private VerifyEmailRepo verifyEmailRepo;
    @Mock
    RestClient restClient;

    @InjectMocks
    private VerifyEmailServiceImpl verifyEmailService;

    @Test
    void verifyByTokenNotExpiredTokenTest() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MAX);
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        doNothing().when(restClient).addUserToSystemChat(1L);
        verifyEmailService.verifyByToken(1L, "token");
        verify(verifyEmailRepo).deleteVerifyEmailByTokenAndUserId(1L, "token");
    }

    @Test
    void verifyByTokenExpiredTokenTest() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MIN);
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        Assertions.assertThrows(UserActivationEmailTokenExpiredException.class,
            () -> verifyEmailService.verifyByToken(1L, "token"));
    }

    /*
     * @Test void deleteAllUsersThatDidNotVerifyEmailTest() {
     * verifyEmailService.deleteAllUsersThatDidNotVerifyEmail();
     * verify(verifyEmailRepo).deleteAllUsersThatDidNotVerifyEmail(); }
     */
}
