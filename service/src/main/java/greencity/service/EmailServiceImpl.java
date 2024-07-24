package greencity.service;

import greencity.constant.EmailConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.eventcomment.EventCommentForSendEmailDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.entity.User;
import greencity.exception.exceptions.LanguageNotSupportedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import greencity.validator.LanguageValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.context.MessageSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final UserRepo userRepo;
    private final Executor executor;
    private final String clientLink;
    private final String ecoNewsLink;
    private final String serverLink;
    private final String senderEmailAddress;
    private static final String PARAM_USER_ID = "&user_id=";
    private final MessageSource messageSource;
    private static final Locale UA_LOCALE = new Locale("uk", "UA");

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
        ITemplateEngine templateEngine,
        UserRepo userRepo,
        @Qualifier("sendEmailExecutor") Executor executor,
        @Value("${client.address}") String clientLink,
        @Value("${econews.address}") String ecoNewsLink,
        @Value("${address}") String serverLink,
        @Value("${sender.email.address}") String senderEmailAddress, MessageSource messageSource) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.executor = executor;
        this.clientLink = clientLink;
        this.ecoNewsLink = ecoNewsLink;
        this.serverLink = serverLink;
        this.senderEmailAddress = senderEmailAddress;
        this.messageSource = messageSource;
    }

    @Override
    public void sendChangePlaceStatusEmail(String authorName, String placeName,
        String placeStatus, String authorEmail) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, placeName);
        User user = userRepo.findByEmail(authorEmail)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        String language = user.getLanguage().getCode();
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, authorName);
        model.put(EmailConstants.PLACE_NAME, placeName);
        model.put(EmailConstants.STATUS, placeStatus);
        model.put(EmailConstants.LANGUAGE, language);

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(authorEmail, messageSource.getMessage(EmailConstants.CHANGE_PLACE_STATUS, null,
            getLocale(language)), template);
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

        for (PlaceAuthorDto subscriber : subscribers) {
            String email = subscriber.getEmail();
            var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
            String language = user.getLanguage().getCode();
            model.put(EmailConstants.USER_NAME, subscriber.getName());
            model.put(EmailConstants.LANGUAGE, language);
            String template = createEmailTemplate(model, EmailConstants.NEW_PLACES_REPORT_EMAIL_PAGE);
            sendEmail(email, messageSource.getMessage(EmailConstants.NEW_PLACES, null, getLocale(language)),
                template);
        }
    }

    @Override
    public void sendNewNewsForSubscriber(List<NewsSubscriberResponseDto> subscribers,
        AddEcoNewsDtoResponse newsDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newsDto);

        for (NewsSubscriberResponseDto dto : subscribers) {
            var user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
            String language = user.getLanguage().getCode();
            model.put(EmailConstants.LANGUAGE, language);
            try {
                model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newsSubscriber/unsubscribe?email="
                    + URLEncoder.encode(dto.getEmail(), StandardCharsets.UTF_8.toString())
                    + "&unsubscribeToken=" + dto.getUnsubscribeToken());
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
            sendEmail(dto.getEmail(), messageSource.getMessage(EmailConstants.NEWS, null, getLocale(language)),
                template);
        }
    }

    @Override
    public void sendNewCommentForEventOrganizer(EventCommentForSendEmailDto dto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.USER_NAME, dto.getOrganizer().getName());
        model.put(EmailConstants.AUTHOR_NAME, dto.getAuthor().getName());
        model.put(EmailConstants.COMMENT_BODY, dto.getText());
        model.put(EmailConstants.COMMENT_TIME, dto.getCreatedDate());
        String eventLink = clientLink + "#/events/" + dto.getEventId();
        model.put(EmailConstants.CLIENT_LINK, eventLink);
        String template = createEmailTemplate(model, EmailConstants.NEW_EVENT_COMMENT_EMAIL_PAGE);
        sendEmail(dto.getEmail(), EmailConstants.EVENT_COMMENT, template);
    }

    @Override
    public void sendCreatedNewsForAuthor(EcoNewsForSendEmailDto newDto) {
        String email = newDto.getAuthor().getEmail();
        var user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        String language = user.getLanguage().getCode();
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newDto);
        model.put(EmailConstants.LANGUAGE, language);
        try {
            model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newSubscriber/unsubscribe?email="
                + URLEncoder.encode(newDto.getAuthor().getEmail(), StandardCharsets.UTF_8.toString())
                + "&unsubscribeToken=" + newDto.getUnsubscribeToken());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.CREATED_NEWS, null, getLocale(language)),
            template);
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
        String baseLink = clientLink + "#/" + (isUbs ? "ubs" : "");
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
        var user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        String language = user.getLanguage().getCode();
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.APPROVE_REGISTRATION, clientLink + "#/auth/restore?" + "token=" + token
            + PARAM_USER_ID + userId);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.USER_APPROVAL_EMAIL_PAGE);
        sendEmail(email, messageSource.getMessage(EmailConstants.APPROVE_REGISTRATION_SUBJECT, null,
            getLocale(language)), template);
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
        model.put(EmailConstants.REASONS, userDeactivationDto.getDeactivationReasons());
        String language = userDeactivationDto.getLang();
        validateLanguage(language);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.REASONS_OF_DEACTIVATION_PAGE);
        sendEmail(userDeactivationDto.getEmail(),
            messageSource.getMessage(EmailConstants.DEACTIVATION, null, getLocale(language)), template);
    }

    @Override
    public void sendMessageOfActivation(UserActivationDto userActivationDto) {
        String language = userActivationDto.getLang();
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userActivationDto.getName());
        validateLanguage(language);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.ACTIVATION_PAGE);
        sendEmail(userActivationDto.getEmail(),
            messageSource.getMessage(EmailConstants.ACTIVATION, null, getLocale(language)), template);
    }

    @Override
    public void sendUserViolationEmail(UserViolationMailDto dto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, dto.getName());
        model.put(EmailConstants.DESCRIPTION, dto.getViolationDescription());
        model.put(EmailConstants.LANGUAGE, dto.getLanguage());
        String language = dto.getLanguage();
        validateLanguage(language);
        model.put(EmailConstants.LANGUAGE, language);
        String template = createEmailTemplate(model, EmailConstants.USER_VIOLATION_PAGE);
        sendEmail(dto.getEmail(), messageSource.getMessage(EmailConstants.VIOLATION_EMAIL, null,
            getLocale(language)), template);
    }

    @Override
    public void sendNotificationByEmail(NotificationDto notification, String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            sendEmail(email, notification.getTitle(), notification.getBody());
        } else {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
        }
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

    @Override
    public void sendEventCreationNotification(String email, String messageBody) {
        String subject = "Notification about event creation status";
        sendEmail(email, subject, messageBody);
    }

    /**
     * {@inheritDoc}
     *
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

    private static Locale getLocale(String language) {
        switch (language) {
            case "ua":
                return UA_LOCALE;
            case "en":
                return Locale.ENGLISH;
            default:
                throw new IllegalStateException("Unexpected value: " + language);
        }
    }
}
