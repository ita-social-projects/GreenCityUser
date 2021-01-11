package greencity.receiver;

import greencity.message.PasswordRecoveryMessage;
import greencity.message.UserApprovalMessage;
import greencity.message.VerifyEmailMessage;
import greencity.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class EmailMessageReceiverTest {
    @InjectMocks
    private EmailMessageReceiver receiver;
    @Mock
    private EmailService service;

    @BeforeEach
    public void setup() {
        initMocks(this);
        receiver = new EmailMessageReceiver(service);
    }

    @Test
    void sendPasswordRecoveryEmail() {
        PasswordRecoveryMessage prm = new PasswordRecoveryMessage(1L, "Oleh",
            "ff@ff.su", "token", "ua");

        receiver.sendPasswordRecoveryEmail(prm);
        verify(service)
            .sendRestoreEmail(prm.getUserId(), prm.getUserFirstName(), prm.getUserEmail(),
                prm.getRecoveryToken(), prm.getLanguage());
    }

    @Test
    void sendVerifyEmail() {
        VerifyEmailMessage vem = new VerifyEmailMessage(1L, "Oleh", "ff@ff.su",
            "token", "ua");

        receiver.sendVerifyEmail(vem);
        verify(service)
            .sendVerificationEmail(vem.getId(), vem.getName(), vem.getEmail(),
                 vem.getToken(), vem.getLanguage());
    }

    @Test
    void sendRegistrationApprovalEmail() {
        UserApprovalMessage uam = new UserApprovalMessage(1L, "Oleh", "ff@ff.su",
            "token");

        receiver.sendRegistrationApprovalEmail(uam);
        verify(service)
            .sendApprovalEmail(uam.getId(), uam.getName(), uam.getEmail(), uam.getToken());
    }
}