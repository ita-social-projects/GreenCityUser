package greencity.service;

import greencity.constant.EmailConstants;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.exception.exceptions.LanguageNotSupportedException;
import greencity.message.ChangePlaceStatusDto;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.ScheduledEmailMessage;
import greencity.message.UserTaggedInCommentMessage;
import greencity.validator.EmailAddressValidator;
import greencity.validator.LanguageValidationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
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
    private final String serverLink;
    private final String senderEmailAddress;
    private final MessageSource messageSource;
    private static final String PARAM_USER_ID = "&user_id=";
    private static final Locale UA_LOCALE = Locale.of("uk", "UA");

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
        ITemplateEngine templateEngine,
        @Qualifier("sendEmailExecutor") Executor executor,
        @Value("${client.address}") String clientLink,
        @Value("${address}") String serverLink,
        @Value("${sender.email.address}") String senderEmailAddress, MessageSource messageSource) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.clientLink = clientLink;
        this.serverLink = serverLink;
        this.senderEmailAddress = senderEmailAddress;
        this.messageSource = messageSource;
    }

    @Override
    public void sendChangePlaceStatusEmail(ChangePlaceStatusDto changePlaceStatus) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, changePlaceStatus.getPlaceName());
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.LANGUAGE, changePlaceStatus.getAuthorLanguage());
        model.put(EmailConstants.USER_NAME, changePlaceStatus.getAuthorFirstName());
        model.put(EmailConstants.PLACE_NAME, changePlaceStatus.getPlaceName());
        model.put(EmailConstants.STATUS, changePlaceStatus.getPlaceStatus().name());
        // TODO change later
        model.put(EmailConstants.UNSUBSCRIBE_LINK, "https://example.com");

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(changePlaceStatus.getAuthorEmail(), messageSource.getMessage(EmailConstants.CHANGE_PLACE_STATUS, null,
            getLocale(changePlaceStatus.getAuthorLanguage())), template);
    }

    @Override
    public void sendAddedNewPlacesReportEmail(List<PlaceAuthorDto> subscribers,
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlaces,
        String notification) {
        log.info(LogMessage.IN_SEND_ADDED_NEW_PLACES_REPORT_EMAIL, null, null, notification);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.RESULT, categoriesWithPlaces);
        model.put(EmailConstants.REPORT_TYPE, notification);

        for (PlaceAuthorDto user : subscribers) {
            model.put(EmailConstants.USER_NAME, user.getName());
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
            // TODO change later
            model.put(EmailConstants.UNSUBSCRIBE_LINK, "https://example.com");
            model.put(EmailConstants.USER_NAME, subscriber.getName());
            model.put(EmailConstants.LANGUAGE, subscriber.getLanguage());
            String template = createEmailTemplate(model, EmailConstants.RECEIVE_INTERESTING_NEWS_EMAIL_PAGE);
            sendEmail(subscriber.getEmail(), messageSource.getMessage(EmailConstants.INTERESTING_ECO_NEWS, null,
                getLocale(subscriber.getLanguage())), template);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Volodymyr Turko
     */
    @Override
    public void sendVerificationEmail(Long id, String name, String email, String token, String language,
        boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        String baseLink = clientLink + "/#" + (isUbs ? "/ubs" : "");
        model.put(EmailConstants.CLIENT_LINK, baseLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.VERIFY_ADDRESS, baseLink + "?token=" + token + PARAM_USER_ID + id);
        validateLanguage(language);
        model.put(EmailConstants.IS_UBS, isUbs);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.VERIFY_EMAIL_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.VERIFY_EMAIL, null, getLocale(language)),
            template);
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
        model.put(EmailConstants.APPROVE_REGISTRATION, clientLink + "#/auth/restore?" + "token=" + token
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
    public void sendRestoreEmail(Long userId, String userName, String userEmail, String token, String language,
        boolean isUbs) {
        Map<String, Object> model =
            buildModelMapForPasswordRestore(userId, userName, token, language, isUbs);
        String template = createEmailTemplate(model, EmailConstants.RESTORE_EMAIL_PAGE);
        sendEmail(userEmail, messageSource.getMessage(EmailConstants.CONFIRM_RESTORING_PASS, null, getLocale(language)),
            template);
    }

    /**
     * This method validates language.
     *
     * @param language language which will be used for sending letter.
     */
    private void validateLanguage(String language) {
        if (!LanguageValidationUtils.isValid(language)) {
            throw new LanguageNotSupportedException("Invalid language");
        }
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

    @Override
    public void sendHabitNotification(String name, String email) {
        String subject = "Notification about not marked habits";
        String content = "Dear " + name + ", you haven't marked any habit during last 3 days";
        sendEmail(email, subject, content);
    }

    @Override
    public void sendReasonOfDeactivation(UserDeactivationReasonDto userDeactivationDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userDeactivationDto.getName());
        model.put(EmailConstants.REASON, userDeactivationDto.getDeactivationReasons().getFirst());
        validateLanguage(userDeactivationDto.getLang());
        String template = createEmailTemplate(model, EmailConstants.REASONS_OF_DEACTIVATION_PAGE);
        sendEmail(userDeactivationDto.getEmail(), EmailConstants.DEACTIVATION, template);
    }

    @Override
    public void sendMessageOfActivation(UserActivationDto userActivationDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userActivationDto.getName());
        validateLanguage(userActivationDto.getLang());
        String template = createEmailTemplate(model, EmailConstants.ACTIVATION_PAGE);
        sendEmail(userActivationDto.getEmail(), EmailConstants.ACTIVATION, template);
    }

    @Override
    public void sendUserViolationEmail(UserViolationMailDto dto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, dto.getName());
        model.put(EmailConstants.DESCRIPTION, dto.getViolationDescription());
        model.put(EmailConstants.LANGUAGE, dto.getLanguage());
        validateLanguage(dto.getLanguage());
        String template = createEmailTemplate(model, EmailConstants.USER_VIOLATION_PAGE);
        sendEmail(dto.getEmail(), EmailConstants.VIOLATION_EMAIL, template);
    }

    @Override
    public void sendSuccessRestorePasswordByEmail(String email, String language, String userName, boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        String baseLink = clientLink + "/#" + (isUbs ? "/ubs" : "");
        model.put(EmailConstants.CLIENT_LINK, baseLink);
        model.put(EmailConstants.USER_NAME, userName);
        validateLanguage(language);
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.IS_UBS, isUbs);
        String template = createEmailTemplate(model, EmailConstants.SUCCESS_RESTORED_PASSWORD_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.RESTORED_PASSWORD, null, getLocale(language)),
            template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yurii Midianyi
     */
    @Override
    public void sendEmailNotification(GeneralEmailMessage notification) {
        sendEmail(notification.getEmail(), notification.getSubject(), notification.getMessage());
    }

    private static Locale getLocale(String language) {
        return switch (language) {
            case "ua" -> UA_LOCALE;
            case "en" -> Locale.ENGLISH;
            default -> throw new IllegalStateException("Unexpected value: " + language);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendHabitAssignNotificationEmail(HabitAssignNotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        String baseLink = clientLink + "/#/profile";
        String language = message.getLanguage();
        validateLanguage(language);
        model.put(EmailConstants.CLIENT_LINK, baseLink);
        model.put(EmailConstants.USER_NAME, message.getReceiverName());
        model.put(EmailConstants.VERIFY_ADDRESS, serverLink + "/habit/assign/confirm/" + message.getHabitAssignId());
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.IS_UBS, false);
        model.put(EmailConstants.SENDER_NAME, message.getSenderName());
        model.put(EmailConstants.HABIT_NAME, message.getHabitName());
        String template = createEmailTemplate(model, EmailConstants.HABIT_ASSIGN_FRIEND_REQUEST_PAGE);
        sendEmail(message.getReceiverEmail(), messageSource.getMessage(EmailConstants.HABIT_ASSIGN_FRIEND_REQUEST,
            null, getLocale(language)), template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendCreateNewPasswordForEmployee(Long employeeId, String employeeFistName, String employeeEmail,
        String token,
        String language, boolean isUbs) {
        Map<String, Object> model =
            buildModelMapForPasswordRestore(employeeId, employeeFistName, token, language, isUbs);
        String template = createEmailTemplate(model, EmailConstants.CRETE_PASSWORD_PAGE);
        String emailSubject = isUbs ? EmailConstants.CONFIRM_CREATING_PASS_UBS : EmailConstants.CONFIRM_CREATING_PASS;
        sendEmail(employeeEmail,
            messageSource.getMessage(emailSubject, null, getLocale(language)),
            template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTaggedUserInCommentNotificationEmail(UserTaggedInCommentMessage message) {
        Map<String, Object> model = new HashMap<>();
        String language = message.getLanguage();
        validateLanguage(language);
        model.put(EmailConstants.CLIENT_LINK, message.getBaseLink());
        model.put(EmailConstants.USER_NAME, message.getReceiverName());
        model.put(EmailConstants.AUTHOR_NAME, message.getTaggerName());
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.IS_UBS, false);
        model.put(EmailConstants.ELEMENT_NAME, message.getElementName());
        model.put(EmailConstants.COMMENT_TIME, message.getCreationDate());
        model.put(EmailConstants.COMMENT_BODY, message.getCommentText());
        String template = createEmailTemplate(model, EmailConstants.USER_TAGGED_IN_COMMENT_PAGE);
        sendEmail(message.getReceiverEmail(), messageSource.getMessage(EmailConstants.USER_TAGGED_IN_COMMENT_REQUEST,
            null, getLocale(language)) + " " + message.getElementName(), template);
    }

    public void sendScheduledNotificationEmail(ScheduledEmailMessage message) {
        Map<String, Object> model = new HashMap<>();
        String language = message.getLanguage();
        validateLanguage(language);
        model.put(EmailConstants.CLIENT_LINK, message.getBaseLink());
        model.put(EmailConstants.USER_NAME, message.getUsername());
        model.put(EmailConstants.LANGUAGE, language);
        model.put(EmailConstants.IS_UBS, false);
        model.put(EmailConstants.TITLE, message.getSubject());
        model.put(EmailConstants.BODY, message.getBody());
        String template = createEmailTemplate(model, EmailConstants.SCHEDULED_NOTIFICATION_PAGE);
        sendEmail(message.getEmail(), message.getSubject(), template);
    }

    private Map<String, Object> buildModelMapForPasswordRestore(Long userId, String name, String token, String language,
        boolean isUbs) {
        Map<String, Object> model = new HashMap<>();
        String baseLink = clientLink + "/#" + (isUbs ? "/ubs" : "");
        model.put(EmailConstants.CLIENT_LINK, baseLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.RESTORE_PASS, baseLink + "/auth/restore?" + "token=" + token
            + PARAM_USER_ID + userId);
        validateLanguage(language);
        model.put(EmailConstants.IS_UBS, isUbs);
        model.put(EmailConstants.LANGUAGE, language);
        return model;
    }
}