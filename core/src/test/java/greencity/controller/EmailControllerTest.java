package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserTelegramFeedbackDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.enums.PlaceStatus;
import greencity.message.PlaceStatusChangeDto;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.service.EmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {
    private static final String LINK = "/email";
    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(emailController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void sendInterestingEcoNews() throws Exception {
        String content = """
            {
                "ecoNewsList": [
                    {
                        "ecoNewsId": 1,
                        "imagePath": "https://google.com",
                        "title": "Title",
                        "text": "Text"
                    }
                ],
                "subscribers": [
                    {
                        "name": "Ilia",
                        "email": "email@gmail.com",
                        "language": "uk",
                        "unsubscribeToken": "d1d3a8b9-2488-48b5-9c7a-3d0b2896063b"
                    }
                ]
            }
            """;

        mockPerform(content, "/sendInterestingEcoNews");

        ObjectMapper objectMapper = new ObjectMapper();
        InterestingEcoNewsDto message = objectMapper.readValue(content, InterestingEcoNewsDto.class);

        verify(emailService).sendInterestingEcoNews(message);
    }

    @Test
    void sendReport() throws Exception {
        String content = """
            {
                "categoriesDtoWithPlacesDtoMap": {
                    "additionalProp1": [
                        {
                            "category": {
                                "name": "string",
                                "parentCategoryId": 0
                            },
                            "name": "string"
                        }
                    ],
                    "additionalProp2": [
                        {
                            "category": {
                                "name": "string",
                                "parentCategoryId": 0
                            },
                            "name": "string"
                        }
                    ]
                },
                "periodicity": "WEEKLY",
                "subscribers": [
                    {
                        "email": "string",
                        "name": "string",
                        "language": "en"
                    }
                ]
            }
            """;

        mockPerform(content, "/sendReport");

        SendReportEmailMessage message = new ObjectMapper().readValue(content, SendReportEmailMessage.class);

        verify(emailService).sendAddedNewPlacesReportEmail(message);
    }

    @Test
    void sendHabitNotification() throws Exception {
        String content = """
            {\
            "email":"string",\
            "name":"string"\
            }\
            """;

        mockPerform(content, "/sendHabitNotification");

        SendHabitNotification notification =
            new ObjectMapper().readValue(content, SendHabitNotification.class);

        verify(emailService).sendHabitNotification(notification.getName(), notification.getEmail());
    }

    private void mockPerform(String content, String subLink) throws Exception {
        mockMvc.perform(post(LINK + subLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    void sendUserViolationEmailTest() throws Exception {
        String content = """
            {\
            "name":"String",\
            "email":"String@gmail.com",\
            "violationDescription":"string string"\
            }\
            """;

        mockPerform(content, "/sendUserViolation");

        UserViolationMailDto userViolationMailDto = new ObjectMapper().readValue(content, UserViolationMailDto.class);
        verify(emailService).sendUserViolationEmail(userViolationMailDto);
    }

    @Test
    @SneakyThrows
    void sendUserReceivedScheduledNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .userId(5L)
            .userUuid("uuid")
            .subject("test subject")
            .baseLink("test link")
            .language("en")
            .build();
        String content = objectMapper.writeValueAsString(message);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/scheduled/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void sendPlaceStatusChangeTest() {
        PlaceStatusChangeDto dto = new PlaceStatusChangeDto();
        dto.setUserName("John Doe");
        dto.setEmail("test@example.com");
        dto.setPlaceName("Green Park");
        dto.setNewStatus(PlaceStatus.APPROVED);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/sendPlaceStatusChange")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer your_token_here")
            .content(content))
            .andExpect(status().isOk());

        verify(emailService).sendPlaceStatusChangeNotification(dto);
    }

    @Test
    @SneakyThrows
    void sendGreenOfficeRequestNotificationWithValidParamsTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test@test.com")
            .username("John Doe")
            .subject("some subject")
            .language("uk")
            .build();
        String content = objectMapper.writeValueAsString(message);

        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/greenoffice/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(emailService, times(1)).sendGreenOfficeRequestEmailToManager(message);
    }

    @Test
    void testSendTelegramFeedback_Success() throws Exception {
        UserTelegramFeedbackDto dto = new UserTelegramFeedbackDto();
        dto.setChatId("12345");
        dto.setName("John Doe");
        dto.setRating(5);
        dto.setComment("Great service!");
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/telegram-feedback")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(emailService, times(1)).sendTelegramFeedbackEmail(dto);
    }

    @Test
    @SneakyThrows
    void sendReasonOfDeactivationTest() {
        UserDeactivationReasonDto dto = new UserDeactivationReasonDto();
        dto.setDeactivationReason("test reason");
        dto.setName("John Doe");
        dto.setEmail("test@example.com");
        dto.setLang("en");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/sendReasonOfDeactivation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer your_token_here")
                .content(content))
            .andExpect(status().isOk());

        verify(emailService).sendReasonOfDeactivation(dto);
    }

    @Test
    @SneakyThrows
    void sendMessageOfActivation() {
        UserActivationDto dto = new UserActivationDto();
        dto.setName("John Doe");
        dto.setEmail("test@example.com");
        dto.setLang("en");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/sendMessageOfActivation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer your_token_here")
                .content(content))
            .andExpect(status().isOk());

        verify(emailService).sendMessageOfActivation(dto);
    }
}
