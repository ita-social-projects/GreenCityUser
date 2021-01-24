package greencity.receiver;

import greencity.message.*;
import greencity.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ message receiver that is used for listening to email sending-related
 * queues.
 */
@Component
public class EmailMessageReceiver {
    private static final String PASSWORD_RECOVERY_QUEUE = "password-recovery-queue";
    public static final String VERIFY_EMAIL_ROUTING_QUEUE = "verify-email-queue";
    public static final String FINISH_USER_APPROVAL_QUEUE = "finish-user-approval";
    private final EmailService emailService;

    /**
     * Constructor with {@link EmailService} dependency declaration, which is used
     * for email sending logic.
     *
     * @param emailService service that is used for email sending logic.
     */
    public EmailMessageReceiver(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Method that is invoked on {@link PasswordRecoveryMessage} receiving. It is
     * responsible for sending password recovery emails.
     */
    @RabbitListener(queues = PASSWORD_RECOVERY_QUEUE)
    public void sendPasswordRecoveryEmail(PasswordRecoveryMessage message) {
        emailService.sendRestoreEmail(
            message.getUserId(),
            message.getUserFirstName(),
            message.getUserEmail(),
            message.getRecoveryToken(),
            message.getLanguage());
    }

    /**
     * Method that is invoked on {@link VerifyEmailMessage} receiving. It is
     * responsible for sending verify email.
     */
    @RabbitListener(queues = VERIFY_EMAIL_ROUTING_QUEUE)
    public void sendVerifyEmail(VerifyEmailMessage message) {
        emailService.sendVerificationEmail(
            message.getId(),
            message.getName(),
            message.getEmail(),
            message.getToken(),
            message.getLanguage());
    }

    /**
     * Method that is invoked on receiving. It is responsible for sending user
     * approval emails.
     */
    @RabbitListener(queues = FINISH_USER_APPROVAL_QUEUE)
    public void sendRegistrationApprovalEmail(UserApprovalMessage message) {
        emailService.sendApprovalEmail(message.getId(), message.getName(), message.getEmail(), message.getToken());
    }
}
