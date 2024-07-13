package greencity.service;

import greencity.ModelUtils;
import greencity.constant.EmailConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.exception.exceptions.LanguageNotSupportedException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import java.util.*;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class EmailServiceImplTest {
    private EmailService service;
    private PlaceAuthorDto placeAuthorDto;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;
    @Mock
    private MessageSource messageSource;
    private static final Locale UA_LOCALE = new Locale("uk", "UA");

    @BeforeEach
    public void setup() {
        initMocks(this);
        service = new EmailServiceImpl(javaMailSender, templateEngine, Executors.newCachedThreadPool(),
            "http://localhost:4200", "http://localhost:4200", "http://localhost:8080",
            "test@email.com", messageSource);
        placeAuthorDto = PlaceAuthorDto.builder()
            .id(1L)
            .email("testEmail@gmail.com")
            .name("testName")
            .build();
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void sendChangePlaceStatusEmailTest() {
        String authorFirstName = "test author first name";
        String placeName = "test place name";
        String placeStatus = "test place status";
        String authorEmail = "useremail@gmail.com";
        service.sendChangePlaceStatusEmail(authorFirstName, placeName, placeStatus, authorEmail);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendAddedNewPlacesReportEmailTest() {
        CategoryDto testCategory = CategoryDto.builder().name("CategoryName").build();
        PlaceNotificationDto testPlace1 =
            PlaceNotificationDto.builder().name("PlaceName1").category(testCategory).build();
        PlaceNotificationDto testPlace2 =
            PlaceNotificationDto.builder().name("PlaceName2").category(testCategory).build();
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlacesTest = new HashMap<>();
        categoriesWithPlacesTest.put(testCategory, Arrays.asList(testPlace1, testPlace2));
        service.sendAddedNewPlacesReportEmail(
            Collections.singletonList(placeAuthorDto), categoriesWithPlacesTest, "DAILY");
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendCreatedNewsForAuthorTest() {
        EcoNewsForSendEmailDto dto = new EcoNewsForSendEmailDto();
        PlaceAuthorDto placeAuthorDto = new PlaceAuthorDto();
        placeAuthorDto.setEmail("test@gmail.com");
        dto.setAuthor(placeAuthorDto);
        service.sendCreatedNewsForAuthor(dto);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendNewNewsForSubscriber() {
        List<NewsSubscriberResponseDto> newsSubscriberResponseDtos =
            Collections.singletonList(new NewsSubscriberResponseDto("test@gmail.com", "someUnsubscribeToken"));
        AddEcoNewsDtoResponse addEcoNewsDtoResponse = ModelUtils.getAddEcoNewsDtoResponse();
        service.sendNewNewsForSubscriber(newsSubscriberResponseDtos, addEcoNewsDtoResponse);
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
        assertThrows(LanguageNotSupportedException.class,
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
        when(messageSource.getMessage(EmailConstants.CONFIRM_CREATING_PASS, null, getLocale(language)))
            .thenReturn("Confirm restoring password");
        service.sendRestoreEmail(id, name, email, token, language, isUbs);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendRestoreEmailLanguageNotFoundException() {
        assertThrows(LanguageNotSupportedException.class,
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
        List<String> test = List.of("test", "test");
        UserDeactivationReasonDto test1 = UserDeactivationReasonDto.builder()
            .deactivationReasons(test)
            .lang("en")
            .email("test@ukr.net")
            .name("test")
            .build();
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
        service.sendMessageOfActivation(test1);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendUserViolationEmailTest() {
        UserViolationMailDto dto = ModelUtils.getUserViolationMailDto();
        service.sendUserViolationEmail(dto);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendUserViolationEmailWithEmptyLanguageTest() {
        UserViolationMailDto dto = ModelUtils.getUserViolationMailDto();
        dto.setLanguage("");
        assertThrows(LanguageNotSupportedException.class, () -> service.sendUserViolationEmail(dto));
        dto.setLanguage(null);
        assertThrows(LanguageNotSupportedException.class, () -> service.sendUserViolationEmail(dto));
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
        assertThrows(LanguageNotSupportedException.class, () -> service.sendUserViolationEmail(dto));
    }

    @Test
    void sendEmailNotificationTest() {
        assertDoesNotThrow(() -> service
            .sendEmailNotification(new GeneralEmailMessage("test@gmail.com", "testSubject", "testMessage")));
        await().atMost(5, SECONDS)
            .untilAsserted(() -> javaMailSender.send(any(MimeMessage.class)));
    }

    @Test
    void sendEmailNotificationToNullEmailTest() {
        GeneralEmailMessage emailMessage = new GeneralEmailMessage(null, "testSubject", "testMessage");
        assertThrows(WrongEmailException.class, () -> service.sendEmailNotification(emailMessage));
    }

    @ParameterizedTest
    @CsvSource(value = {"ua", "en"})
    void sendHabitAssignNotificationEmail(String language) {
        HabitAssignNotificationMessage message = HabitAssignNotificationMessage.builder()
            .language(language)
            .habitAssignId(100L)
            .habitName("TEST")
            .receiverEmail("test@gmail.com")
            .receiverName("TEST")
            .senderName("TEST")
            .build();
        when(messageSource.getMessage(EmailConstants.HABIT_ASSIGN_FRIEND_REQUEST, null, getLocale(language)))
            .thenReturn("Habit friend request");

        service.sendHabitAssignNotificationEmail(message);
        verify(javaMailSender).createMimeMessage();
        verify(messageSource).getMessage(EmailConstants.HABIT_ASSIGN_FRIEND_REQUEST, null, getLocale(language));
    }

    private static Locale getLocale(String language) {
        return switch (language) {
            case "ua" -> UA_LOCALE;
            case "en" -> Locale.ENGLISH;
            default -> throw new IllegalStateException("Unexpected value: " + language);
        };
    }
}
