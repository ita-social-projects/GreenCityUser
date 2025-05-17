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
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.PlaceStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.message.PlaceStatusChangeDto;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendReportEmailMessage;
import greencity.repository.UserRepo;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static greencity.ModelUtils.getSubscriberDto;

import static greencity.TestConst.ENGLISH_CODE;
import static greencity.TestConst.NAME;
import static greencity.TestConst.EMAIL;
import static greencity.TestConst.PLACE_NAME;
import static greencity.TestConst.SIMPLE_LONG_NUMBER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;

import org.thymeleaf.context.Context;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceImplTest {
    EmailService service;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    ITemplateEngine templateEngine;

    @Mock
    MessageSource messageSource;

    @Mock
    UserRepo userRepo;

    static final Locale UA_LOCALE = Locale.of("uk", "UA");

    @BeforeEach
    public void setup() {
        service = new EmailServiceImpl(
            javaMailSender,
            templateEngine,
            Executors.newCachedThreadPool(),
            "http://localhost:4200",
            "test@email.com",
            messageSource,
            userRepo);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html></html>");
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
            .periodicity(EmailPreferencePeriodicity.WEEKLY)
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
    void sendScheduledNotificationEmailTestWhenUserIdIsPresent() {
        Long userId = 4L;
        String absentUuid = null;
        Optional<String> emailOptional = Optional.of("email@email.com");
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .userId(userId)
            .userUuid(absentUuid)
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();

        when(userRepo.findEmailById(userId))
            .thenReturn(emailOptional);

        service.sendScheduledNotificationEmail(message);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendScheduledNotificationEmailTestWhenUserUuidIsPresent() {
        Long absentUserId = null;
        String uuid = "uuid";
        Optional<String> emailOptional = Optional.of("email@email.com");
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .userId(absentUserId)
            .userUuid(uuid)
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();

        when(userRepo.findEmailByUuid(uuid))
            .thenReturn(emailOptional);

        service.sendScheduledNotificationEmail(message);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendScheduledNotificationEmailTestWhenUserNotFoundById() {
        Long userId = 4L;
        String absentUuid = null;
        Optional<String> emptyEmailOptional = Optional.empty();
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .userId(userId)
            .userUuid(absentUuid)
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();

        when(userRepo.findEmailById(userId))
            .thenReturn(emptyEmailOptional);

        assertThrows(
            NotFoundException.class,
            () -> service.sendScheduledNotificationEmail(message));
        verify(javaMailSender, never()).createMimeMessage();
    }

    @Test
    void sendScheduledNotificationEmailTestWhenUserNotFoundByUuid() {
        Long absentUserId = null;
        String uuid = "uuid";
        Optional<String> emptyEmailOptional = Optional.empty();
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .userId(absentUserId)
            .userUuid(uuid)
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();

        when(userRepo.findEmailByUuid(uuid))
            .thenReturn(emptyEmailOptional);

        assertThrows(
            NotFoundException.class,
            () -> service.sendScheduledNotificationEmail(message));
        verify(javaMailSender, never()).createMimeMessage();
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

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ua, false",
        "1, Test, test@gmail.com, token, en, true"})
    void sendBlockAccountNotificationWithUnblockLinkEmailTest(Long id, String name, String email,
        String token, String language,
        Boolean isUbs) {
        when(messageSource.getMessage(EmailConstants.BLOCKED_USER, null, getLocale(language)))
            .thenReturn("Your account is blocked");

        service.sendBlockAccountNotificationWithUnblockLinkEmail(id, name, email, token, language, isUbs);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendPlaceStatusChangeNotificationTest() throws InterruptedException {
        PlaceStatusChangeDto dto = new PlaceStatusChangeDto();
        dto.setUserName(NAME);
        dto.setPlaceName(PLACE_NAME);
        dto.setNewStatus(PlaceStatus.APPROVED);
        dto.setEmail(EMAIL);
        User user = new User();
        user.setEmail(EMAIL);
        user.setName(NAME);
        Language language = new Language(SIMPLE_LONG_NUMBER, ENGLISH_CODE, List.of(user));
        user.setLanguage(language);
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(javaMailSender).send(any(MimeMessage.class));
        String subject = "Place Status Change Notification";
        when(messageSource.getMessage(eq(EmailConstants.UPDATE_STATUS), any(), eq(getLocale(ENGLISH_CODE))))
            .thenReturn(subject);

        service.sendPlaceStatusChangeNotification(dto);
        latch.await();
        verify(userRepo).findByEmail(dto.getEmail());
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
        verify(messageSource).getMessage(eq(EmailConstants.UPDATE_STATUS), any(), eq(getLocale(ENGLISH_CODE)));
    }

    @Test
    void sendPlaceStatusChangeNotificationUserNotFoundTest() {
        PlaceStatusChangeDto dto = new PlaceStatusChangeDto();
        dto.setUserName(NAME);
        dto.setPlaceName(PLACE_NAME);
        dto.setNewStatus(PlaceStatus.APPROVED);
        dto.setEmail(EMAIL);
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            service.sendPlaceStatusChangeNotification(dto);
        });
        verify(userRepo).findByEmail(dto.getEmail());
    }

    private static Locale getLocale(String language) {
        return switch (language) {
            case "ua" -> UA_LOCALE;
            case "en" -> Locale.ENGLISH;
            default -> throw new IllegalStateException("Unexpected value: " + language);
        };
    }
}
