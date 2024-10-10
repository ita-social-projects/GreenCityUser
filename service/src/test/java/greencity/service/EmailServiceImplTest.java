package greencity.service;

import greencity.ModelUtils;
import greencity.constant.EmailConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.SubscriberDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.PlaceStatus;
import greencity.exception.exceptions.WrongEmailException;
import greencity.message.ChangePlaceStatusDto;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import java.util.*;
import java.util.concurrent.Executors;

import static greencity.ModelUtils.getSubscriberDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.thymeleaf.context.Context;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceImplTest {
    private EmailService service;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;
    @Mock
    private MessageSource messageSource;
    private static final Locale UA_LOCALE = Locale.of("uk", "UA");

    @BeforeEach
    public void setup() {
        service = new EmailServiceImpl(javaMailSender, templateEngine, Executors.newCachedThreadPool(),
            "http://localhost:4200",
            "test@email.com", messageSource);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html></html>");
    }

    @Test
    void sendChangePlaceStatusEmailTest() {
        ChangePlaceStatusDto changePlaceStatusDto = ChangePlaceStatusDto.builder()
            .placeStatus(PlaceStatus.APPROVED)
            .authorEmail("useremail@gmail.com")
            .placeName("test place name")
            .authorFirstName("test author first name")
            .authorLanguage("en")
            .build();
        when(messageSource.getMessage(EmailConstants.CHANGE_PLACE_STATUS, null,
            getLocale(changePlaceStatusDto.getAuthorLanguage())))
                .thenReturn("Change place status");
        service.sendChangePlaceStatusEmail(changePlaceStatusDto);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendAddedNewPlacesReportEmailTest() {
        SendReportEmailMessage sendReportEmailMessage = SendReportEmailMessage.builder()
            .subscribers(List.of(getSubscriberDto()))
            .categoriesDtoWithPlacesDtoMap(Map.of(
                CategoryDto.builder()
                    .name("Cycling routes")
                    .build(),
                List.of(
                    PlaceNotificationDto.builder()
                        .name("Central Park")
                        .category(CategoryDto.builder()
                            .name("Hotels")
                            .build())
                        .build())))
            .emailPreferencePeriodicity(EmailPreferencePeriodicity.WEEKLY)
            .build();
        service.sendAddedNewPlacesReportEmail(sendReportEmailMessage);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendInterestingEcoNewsTest() {
        InterestingEcoNewsDto dto = new InterestingEcoNewsDto();
        dto.setSubscribers(List.of(new SubscriberDto("Ilia", "test@gmail.com", "ua", UUID.randomUUID())));

        when(messageSource.getMessage(EmailConstants.INTERESTING_ECO_NEWS, null, getLocale("ua")))
            .thenReturn("Interesting Eco News");

        service.sendInterestingEcoNews(dto);
        verify(javaMailSender).createMimeMessage();
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ua",
        "1, Test, test@gmail.com, token, en"})
    void sendVerificationEmail(Long id, String name, String email, String token, String language) {
        when(messageSource.getMessage(EmailConstants.VERIFY_EMAIL, null, getLocale(language)))
            .thenReturn("Verify your email address");

        service.sendVerificationEmail(id, name, email, token, language, false);
        verify(javaMailSender).createMimeMessage();
        verify(messageSource).getMessage(EmailConstants.VERIFY_EMAIL, null, getLocale(language));
    }

    @Test
    void sendVerificationEmailLanguageNotFoundException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendVerificationEmail(1L, "Test", "test@gmail.com", "token", "enuaru", false));
    }

    @Test
    void sendApprovalEmail() {
        service.sendApprovalEmail(1L, "userName", "test@gmail.com", "someToken");
        verify(javaMailSender).createMimeMessage();
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ua, false",
        "1, Test, test@gmail.com, token, en, false"})
    void sendRestoreEmail(Long id, String name, String email, String token, String language, Boolean isUbs) {
        when(messageSource.getMessage(EmailConstants.CONFIRM_RESTORING_PASS, null, getLocale(language)))
            .thenReturn("Confirm restoring password");
        service.sendRestoreEmail(id, name, email, token, language, isUbs);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendRestoreEmailLanguageNotFoundException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendRestoreEmail(1L, "Test", "test@gmail.com", "token", "enuaru", false));
    }

    @Test
    void sendHabitNotification() {
        service.sendHabitNotification("userName", "userEmail@gmail.com");
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendHabitNotificationWithInvalidEmail() {
        assertThrows(WrongEmailException.class,
            () -> service.sendHabitNotification("userName", "userEmail"));
    }

    @Test
    void sendReasonOfDeactivation() {
        UserDeactivationReasonDto test1 = UserDeactivationReasonDto.builder()
            .deactivationReason("test")
            .lang("en")
            .email("test@ukr.net")
            .name("test")
            .build();
        when(messageSource.getMessage(EmailConstants.DEACTIVATION, null, getLocale(test1.getLang())))
            .thenReturn("Deactivation");
        service.sendReasonOfDeactivation(test1);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendMessageOfActivation() {
        UserActivationDto test1 = UserActivationDto.builder()
            .lang("en")
            .email("test@ukr.net")
            .name("test")
            .build();
        when(messageSource.getMessage(EmailConstants.ACTIVATION, null, getLocale(test1.getLang())))
            .thenReturn("Activation");
        service.sendMessageOfActivation(test1);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendUserViolationEmailTest() {
        UserViolationMailDto dto = ModelUtils.getUserViolationMailDto();
        when(messageSource.getMessage(EmailConstants.VIOLATION_EMAIL, null, getLocale(dto.getLanguage())))
            .thenReturn("Violation email");
        service.sendUserViolationEmail(dto);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendSuccessRestorePasswordByEmailTest() {
        String email = "test@gmail.com";
        String lang = "en";
        String userName = "Helgi";
        boolean isUbs = false;
        when(messageSource.getMessage(EmailConstants.RESTORED_PASSWORD, null, getLocale(lang)))
            .thenReturn("Restore password");
        service.sendSuccessRestorePasswordByEmail(email, lang, userName, isUbs);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendUserViolationEmailWithUnsupportedLanguageTest() {
        UserViolationMailDto dto = ModelUtils.getUserViolationMailDto();
        dto.setLanguage("de");
        assertThrows(IllegalStateException.class, () -> service.sendUserViolationEmail(dto));
    }

    @Test
    void sendScheduledNotificationEmailTest() {
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .email("test@gmail.com")
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();
        service.sendScheduledNotificationEmail(message);
        verify(javaMailSender).createMimeMessage();
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ua, false",
        "1, Test, test@gmail.com, token, en, true"})
    void sendCreateNewPasswordForEmployee(Long id, String name, String email, String token, String language,
        Boolean isUbs) {
        when(messageSource.getMessage(EmailConstants.CONFIRM_CREATING_PASS, null, getLocale(language)))
            .thenReturn("Create password for Green City");
        when(messageSource.getMessage(EmailConstants.CONFIRM_CREATING_PASS_UBS, null, getLocale(language)))
            .thenReturn("Create password for Pick Up City");
        service.sendCreateNewPasswordForEmployee(id, name, email, token, language, isUbs);
        verify(javaMailSender).createMimeMessage();
    }

    private static Locale getLocale(String language) {
        return switch (language) {
            case "ua" -> UA_LOCALE;
            case "en" -> Locale.ENGLISH;
            default -> throw new IllegalStateException("Unexpected value: " + language);
        };
    }
}
