package greencity.service;

import greencity.constant.EmailConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        @Value("${sender.email.address}") String senderEmailAddress) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.executor = executor;
        this.clientLink = clientLink;
        this.ecoNewsLink = ecoNewsLink;
        this.serverLink = serverLink;
        this.senderEmailAddress = senderEmailAddress;
    }

    @Override
    public void sendChangePlaceStatusEmail(String authorName, String placeName,
        String placeStatus, String authorEmail) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, placeName);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, authorName);
        model.put(EmailConstants.PLACE_NAME, placeName);
        model.put(EmailConstants.STATUS, placeStatus);

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(authorEmail, EmailConstants.GC_CONTRIBUTORS, template);
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

    @Override
    public void sendNewNewsForSubscriber(List<NewsSubscriberResponseDto> subscribers,
        AddEcoNewsDtoResponse newsDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newsDto);
        for (NewsSubscriberResponseDto dto : subscribers) {
            try {
                model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newsSubscriber/unsubscribe?email="
                    + URLEncoder.encode(dto.getEmail(), StandardCharsets.UTF_8.toString())
                    + "&unsubscribeToken=" + dto.getUnsubscribeToken());
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
            sendEmail(dto.getEmail(), EmailConstants.NEWS, template);
        }
    }

    @Override
    public void sendCreatedNewsForAuthor(EcoNewsForSendEmailDto newDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newDto);
        try {
            model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newSubscriber/unsubscribe?email="
                + URLEncoder.encode(newDto.getAuthor().getEmail(), StandardCharsets.UTF_8.toString())
                + "&unsubscribeToken=" + newDto.getUnsubscribeToken());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
        sendEmail(newDto.getAuthor().getEmail(), EmailConstants.CREATED_NEWS, template);
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
        changeLocale(language);
        model.put(EmailConstants.IS_UBS, isUbs);
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
        Map<String, Object> model = new HashMap<>();
        String baseLink = clientLink + "/#" + (isUbs ? "/ubs" : "");
        model.put(EmailConstants.CLIENT_LINK, baseLink);
        model.put(EmailConstants.USER_NAME, userName);
        model.put(EmailConstants.RESTORE_PASS, baseLink + "/auth/restore?" + "token=" + token
            + PARAM_USER_ID + userId);
        changeLocale(language);
        model.put(EmailConstants.IS_UBS, isUbs);
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
        changeLocale(userDeactivationDto.getLang());
        String template = createEmailTemplate(model, EmailConstants.REASONS_OF_DEACTIVATION_PAGE);
        sendEmail(userDeactivationDto.getEmail(), EmailConstants.DEACTIVATION, template);
    }

    @Override
    public void sendMessageOfActivation(UserActivationDto userActivationDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userActivationDto.getName());
        changeLocale(userActivationDto.getLang());
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
        changeLocale(dto.getLanguage());
        String template = createEmailTemplate(model, EmailConstants.USER_VIOLATION_PAGE);
        sendEmail(dto.getEmail(), EmailConstants.VIOLATION_EMAIL, template);
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
        changeLocale(language);
        model.put(EmailConstants.IS_UBS, isUbs);
        String template = createEmailTemplate(model, EmailConstants.SUCCESS_RESTORED_PASSWORD_PAGE);
        sendEmail(email, EmailConstants.RESTORED_PASSWORD, template);
    }
}
