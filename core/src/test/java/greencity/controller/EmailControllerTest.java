package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.ScheduledEmailMessage;
import greencity.message.ChangePlaceStatusDto;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.message.UserTaggedInCommentMessage;
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

import java.time.LocalDateTime;

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
                        "language": "ua",
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
                "emailNotification": "WEEKLY",
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
    void changePlaceStatus() throws Exception {
        String content = """
            {\
            "authorEmail":"string",\
            "authorFirstName":"string",\
            "placeName":"string",\
            "placeStatus":"APPROVED",\
            "authorLanguage":"en"\
            }\
            """;

        mockPerform(content, "/changePlaceStatus");

        ChangePlaceStatusDto message = new ObjectMapper().readValue(content, ChangePlaceStatusDto.class);

        verify(emailService).sendChangePlaceStatusEmail(message);
    }

    @Test
    void changePlaceStatusInvalidPlaceStatus() throws Exception {
        String content = """
            {\
            "authorEmail":"string",\
            "authorFirstName":"string",\
            "placeName":"string",\
            "placeStatus":"ggggggg"\
            }\
            """;

        mockMvc.perform(post(LINK + "/changePlaceStatus")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
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
    void sendEmailNotificationTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        GeneralEmailMessage emailMessage = new GeneralEmailMessage("test@example.com", "Test Subject", "Test Message");
        String jsonRequest = objectMapper.writeValueAsString(emailMessage);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/general/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void sendHabitAssignNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        HabitAssignNotificationMessage message = HabitAssignNotificationMessage.builder()
            .language("ua")
            .habitAssignId(100L)
            .habitName("TEST")
            .receiverEmail("test@gmail.com")
            .receiverName("TEST")
            .senderName("TEST")
            .build();
        String content = objectMapper.writeValueAsString(message);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/sendHabitAssignNotification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void sendUserTaggedInCommentNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserTaggedInCommentMessage message = UserTaggedInCommentMessage.builder()
            .receiverEmail("receiver@example.com")
            .receiverName("receiver")
            .elementName("event")
            .commentText("test")
            .taggerName("tagger")
            .commentedElementId(1L)
            .language("en")
            .baseLink("testLink")
            .creationDate(LocalDateTime.now())
            .build();
        String content = objectMapper.writeValueAsString(message);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/taggedUserInComment/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void sendUserReceivedScheduledNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ScheduledEmailMessage message = ScheduledEmailMessage.builder()
            .body("test body")
            .username("test user")
            .email("test@gmail.com")
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
}
