package greencity.service;

import greencity.constant.EmailConstants;
import greencity.constant.LogMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final Executor executor;
    private final String clientLink;
    private final String senderEmailAddress;
    private static final String PARAM_USER_ID = "&user_id=";

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
        ITemplateEngine templateEngine,
        @Qualifier("sendEmailExecutor") Executor executor,
        @Value("${client.address}") String clientLink,
        @Value("${sender.email.address}") String senderEmailAddress) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.clientLink = clientLink;
        this.senderEmailAddress = senderEmailAddress;
    }

    /**
     * {@inheritDoc}
     *
     * @author Volodymyr Turko
     */
    @Override
    public void sendVerificationEmail(Long id, String name, String email, String token, String language) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.VERIFY_ADDRESS, clientLink + "#/?token="
            + token + PARAM_USER_ID + id);
        changeLocale(language);
        log.info(Locale.getDefault().toString());
        String template = createEmailTemplate(model, EmailConstants.VERIFY_EMAIL_PAGE);
        sendEmail(email, EmailConstants.VERIFY_EMAIL, template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Vasyl Zhovnir
     */
    @Override
    public void sendApprovalEmail(Long userId, String name, String email, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.APPROVE_REGISTRATION, clientLink + "/#/auth/restore?" + "token=" + token
            + PARAM_USER_ID + userId);
        String template = createEmailTemplate(model, EmailConstants.USER_APPROVAL_EMAIL_PAGE);
        sendEmail(email, EmailConstants.APPROVE_REGISTRATION_SUBJECT, template);
    }

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId    the user id is used for recovery link building.
     * @param userName  username is used in email model constants.
     * @param userEmail user email which will be used for sending recovery letter.
     * @param token     password recovery token.
     */
    @Override
    public void sendRestoreEmail(Long userId, String userName, String userEmail, String token, String language) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userName);
        model.put(EmailConstants.RESTORE_PASS, clientLink + "#/auth/restore?" + "token=" + token
            + PARAM_USER_ID + userId);
        changeLocale(language);
        log.info(Locale.getDefault().toString());
        String template = createEmailTemplate(model, EmailConstants.RESTORE_EMAIL_PAGE);
        sendEmail(userEmail, EmailConstants.CONFIRM_RESTORING_PASS, template);
    }

    /**
     * {@inheritDoc}
     *
     * @param language language which will be used for sending recovery letter.
     */
    private void changeLocale(String language) {
        Locale rus = new Locale("ru", "RU");
        Locale ua = new Locale("uk", "UA");
        switch (language) {
            case "ua":
                Locale.setDefault(ua);
                break;
            case "ru":
                Locale.setDefault(rus);
                break;
            case "en":
                Locale.setDefault(Locale.ENGLISH);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + language);
        }
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info(LogMessage.IN_CREATE_TEMPLATE_NAME, null, templateName);
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process("email/" + templateName, context);
    }

    private void sendEmail(String receiverEmail, String subject, String content) {
        log.info(LogMessage.IN_SEND_EMAIL, receiverEmail, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(senderEmailAddress);
            mimeMessageHelper.setTo(receiverEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessage.setContent(content, EmailConstants.EMAIL_CONTENT_TYPE);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        executor.execute(() -> javaMailSender.send(mimeMessage));
    }

}
