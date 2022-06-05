package greencity.service;

import greencity.ModelUtils;
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
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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
    private UserRepo userRepo;

    @BeforeEach
    public void setup() {
        initMocks(this);
        service = new EmailServiceImpl(javaMailSender, templateEngine, userRepo, Executors.newCachedThreadPool(),
            "http://localhost:4200", "http://localhost:4200", "http://localhost:8080",
            "test@email.com");
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
        String authorEmail = "test author email";
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
    @CsvSource(value = {"1, Test, test@gmail.com, token, ru",
        "1, Test, test@gmail.com, token, ua",
        "1, Test, test@gmail.com, token, en"})
    void sendVerificationEmail(Long id, String name, String email, String token, String language) {
        service.sendVerificationEmail(id, name, email, token, language, false);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendVerificationEmailIllegalStateException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendVerificationEmail(1L, "Test", "test@gmail.com", "token", "enuaru", false));
    }

    @Test
    void sendApprovalEmail() {
        service.sendApprovalEmail(1L, "userName", "test@gmail.com", "someToken");
        verify(javaMailSender).createMimeMessage();
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ru, true",
        "1, Test, test@gmail.com, token, ua, false",
        "1, Test, test@gmail.com, token, en, false"})
    void sendRestoreEmail(Long id, String name, String email, String token, String language, Boolean isUbs) {
        service.sendRestoreEmail(id, name, email, token, language, isUbs);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendRestoreEmailIllegalStateException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendRestoreEmail(1L, "Test", "test@gmail.com", "token", "enuaru", false));
    }

    @Test
    void sendHabitNotification() {
        service.sendHabitNotification("userName", "userEmail");
        verify(javaMailSender).createMimeMessage();
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
        List<String> test = List.of("test", "test");
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
    void sendSuccessRestorePasswordByEmailTest() {
        String email = "test@gmail.com";
        String lang = "en";
        String userName = "Helgi";
        boolean isUbs = false;
        service.sendSuccessRestorePasswordByEmail(email, lang, userName, isUbs);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendNotificationByEmail() {
        User user = User.builder().build();
        NotificationDto dto = NotificationDto.builder().title("title").body("body").build();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        service.sendNotificationByEmail(dto, "test@gmail.com");
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendNotificationByEmailNotFoundException() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        NotificationDto dto = NotificationDto.builder().title("title").body("body").build();
        assertThrows(NotFoundException.class, () -> service.sendNotificationByEmail(dto, "test@gmail.com"));
    }
}
