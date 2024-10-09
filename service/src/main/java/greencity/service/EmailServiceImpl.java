package greencity.service;

import greencity.constant.EmailConstants;
import greencity.constant.LogMessage;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.ChangePlaceStatusDto;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import greencity.validator.EmailAddressValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import static greencity.enums.SubscriptionType.ECO_NEWS;

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
    private final MessageSource messageSource;
    private static final String PARAM_USER_ID = "&user_id=";

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
        ITemplateEngine templateEngine,
        @Qualifier("sendEmailExecutor") Executor executor,
        @Value("${client.address}") String clientLink,
        @Value("${sender.email.address}") String senderEmailAddress, MessageSource messageSource) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.clientLink = clientLink;
        this.senderEmailAddress = senderEmailAddress;
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendChangePlaceStatusEmail(ChangePlaceStatusDto changePlaceStatus) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, changePlaceStatus.getPlaceName());
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.LANGUAGE, changePlaceStatus.getAuthorLanguage());
        model.put(EmailConstants.USER_NAME, changePlaceStatus.getAuthorFirstName());
        model.put(EmailConstants.PLACE_NAME, changePlaceStatus.getPlaceName());
        model.put(EmailConstants.STATUS, changePlaceStatus.getPlaceStatus().name());
        model.put(EmailConstants.PROFILE_LINK, getProfileLink());

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(changePlaceStatus.getAuthorEmail(), messageSource.getMessage(EmailConstants.CHANGE_PLACE_STATUS, null,
            getLocale(changePlaceStatus.getAuthorLanguage())), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAddedNewPlacesReportEmail(SendReportEmailMessage message) {
        log.info(LogMessage.IN_SEND_ADDED_NEW_PLACES_REPORT_EMAIL, message.getSubscribers(),
            message.getCategoriesDtoWithPlacesDtoMap(), message.getEmailNotification());
        Map<String, Object> sharedModel = new HashMap<>();
        sharedModel.put(EmailConstants.CLIENT_LINK, clientLink);
        sharedModel.put(EmailConstants.RESULT, message.getCategoriesDtoWithPlacesDtoMap());
        sharedModel.put(EmailConstants.REPORT_TYPE, message.getEmailNotification().name());

        for (SubscriberDto user : message.getSubscribers()) {
            Map<String, Object> model = new HashMap<>(sharedModel);
            sharedModel.put(EmailConstants.PROFILE_LINK, getProfileLink());
            model.put(EmailConstants.USER_NAME, user.getName());
            model.put(EmailConstants.LANGUAGE, user.getLanguage());
            String template = createEmailTemplate(model, EmailConstants.NEW_PLACES_REPORT_EMAIL_PAGE);
            sendEmail(user.getEmail(), EmailConstants.NEW_PLACES, template);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendInterestingEcoNews(InterestingEcoNewsDto interestingEcoNews) {
        Map<String, Object> sharedModel = new HashMap<>();
        sharedModel.put(EmailConstants.ECO_NEWS_LIST, interestingEcoNews.getEcoNewsList());
        sharedModel.put(EmailConstants.CLIENT_LINK, clientLink);

        for (SubscriberDto subscriber : interestingEcoNews.getSubscribers()) {
            Map<String, Object> model = new HashMap<>(sharedModel);
            model.put(EmailConstants.UNSUBSCRIBE_LINK, clientLink + "/#/unsubscribe"
                + "?token=" + subscriber.getUnsubscribeToken() + "&type=" + ECO_NEWS);
            model.put(EmailConstants.USER_NAME, subscriber.getName());
            model.put(EmailConstants.LANGUAGE, subscriber.getLanguage());
            String template = createEmailTemplate(model, EmailConstants.RECEIVE_INTERESTING_NEWS_EMAIL_PAGE);
            sendEmail(subscriber.getEmail(), messageSource.getMessage(EmailConstants.INTERESTING_ECO_NEWS, null,
                getLocale(subscriber.getLanguage())), template);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendVerificationEmail(Long id, String name, String email, String token, String language,
        boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, getClientLinkByIsUbs(isUbs));
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.VERIFY_ADDRESS, clientLink + "/#" + (isUbs ? "/ubs" : "") + "?token=" + token
            + PARAM_USER_ID + id);
        model.put(EmailConstants.IS_UBS, isUbs);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.VERIFY_EMAIL_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.VERIFY_EMAIL, null,
            getLocale(language)), template);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public void sendRestoreEmail(Long userId, String userName, String userEmail, String token, String language,
        boolean isUbs) {
        Map<String, Object> model = buildModelMapForPasswordRestore(userId, userName, token, language, isUbs);
        String template = createEmailTemplate(model, EmailConstants.RESTORE_EMAIL_PAGE);
        sendEmail(userEmail, messageSource.getMessage(EmailConstants.CONFIRM_RESTORING_PASS, null,
            getLocale(language)), template);
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info(LogMessage.IN_CREATE_TEMPLATE_NAME, null, templateName);
        Context context = new Context();
        context.setVariables(vars);
        if (vars.get("language") != null) {
            context.setLocale(getLocale((String) vars.get("language")));
        }
        return templateEngine.process("email/" + templateName, context);
    }

    private void sendEmail(String receiverEmail, String subject, String content) {
        EmailAddressValidator.validate(receiverEmail);
        log.info(LogMessage.IN_SEND_EMAIL, receiverEmail, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(senderEmailAddress);
            mimeMessageHelper.setTo(receiverEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        executor.execute(() -> javaMailSender.send(mimeMessage));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendHabitNotification(String name, String email) {
        String subject = "Notification about not marked habits";
        String content = "Dear " + name + ", you haven't marked any habit during last 3 days";
        sendEmail(email, subject, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendReasonOfDeactivation(UserDeactivationReasonDto userDeactivationDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userDeactivationDto.getName());
        model.put(EmailConstants.REASON, userDeactivationDto.getDeactivationReason());
        model.put(EmailConstants.LANGUAGE, userDeactivationDto.getLang());
        String template = createEmailTemplate(model, EmailConstants.REASONS_OF_DEACTIVATION_PAGE);
        sendEmail(userDeactivationDto.getEmail(), messageSource.getMessage(EmailConstants.DEACTIVATION, null,
            getLocale(userDeactivationDto.getLang())), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageOfActivation(UserActivationDto userActivationDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userActivationDto.getName());
        model.put(EmailConstants.LANGUAGE, userActivationDto.getLang());
        String template = createEmailTemplate(model, EmailConstants.ACTIVATION_PAGE);
        sendEmail(userActivationDto.getEmail(), messageSource.getMessage(EmailConstants.ACTIVATION, null,
            getLocale(userActivationDto.getLang())), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUserViolationEmail(UserViolationMailDto dto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, dto.getName());
        model.put(EmailConstants.DESCRIPTION, dto.getViolationDescription());
        model.put(EmailConstants.LANGUAGE, dto.getLanguage());
        String template = createEmailTemplate(model, EmailConstants.USER_VIOLATION_PAGE);
        sendEmail(dto.getEmail(), messageSource.getMessage(EmailConstants.VIOLATION_EMAIL, null,
            getLocale(dto.getLanguage())), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSuccessRestorePasswordByEmail(String email, String language, String userName, boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, getClientLinkByIsUbs(isUbs));
        model.put(EmailConstants.USER_NAME, userName);
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.IS_UBS, isUbs);
        String template = createEmailTemplate(model, EmailConstants.SUCCESS_RESTORED_PASSWORD_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.RESTORED_PASSWORD, null,
            getLocale(language)), template);
    }

    private static Locale getLocale(String language) {
        if (language == null || language.equals("en")) {
            return Locale.ENGLISH;
        } else if (language.equals("ua")) {
            return Locale.of("uk", "UA");
        } else {
            throw new IllegalStateException("Unexpected value: " + language);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendCreateNewPasswordForEmployee(Long employeeId, String employeeFistName, String employeeEmail,
        String token, String language, boolean isUbs) {
        Map<String, Object> model =
            buildModelMapForPasswordRestore(employeeId, employeeFistName, token, language, isUbs);
        String template = createEmailTemplate(model, EmailConstants.CRETE_PASSWORD_PAGE);
        String emailSubject = isUbs ? EmailConstants.CONFIRM_CREATING_PASS_UBS : EmailConstants.CONFIRM_CREATING_PASS;
        sendEmail(employeeEmail, messageSource.getMessage(emailSubject, null, getLocale(language)), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendScheduledNotificationEmail(ScheduledEmailMessage message) {
        Map<String, Object> model = new HashMap<>();
        String language = message.getLanguage();
        model.put(EmailConstants.CLIENT_LINK, message.getBaseLink());
        model.put(EmailConstants.USER_NAME, message.getUsername());
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.TITLE, message.getSubject());
        model.put(EmailConstants.BODY, message.getBody());
        model.put(EmailConstants.PROFILE_LINK, getProfileLink());

        String template = createEmailTemplate(model, EmailConstants.SCHEDULED_NOTIFICATION_PAGE);
        sendEmail(message.getEmail(), message.getSubject(), template);
    }

    private Map<String, Object> buildModelMapForPasswordRestore(Long userId, String name, String token, String language,
        boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, getClientLinkByIsUbs(isUbs));
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.RESTORE_PASS, clientLink + "/#" + (isUbs ? "/ubs" : "") + "/auth/restore?"
            + "token=" + token + PARAM_USER_ID + userId);
        model.put(EmailConstants.IS_UBS, isUbs);
        model.put(EmailConstants.LANGUAGE, language);
        return model;
    }

    private String getClientLinkByIsUbs(boolean isUbs) {
        return clientLink + "/#" + (isUbs ? "/ubs" : "/greenCity");
    }

    private String getProfileLink() {
        return clientLink + "/#/profile";
    }
}
