package greencity.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.*;
import greencity.service.EmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

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
    void addEcoNews() throws Exception {
        String content =
            """
                {"unsubscribeToken":"string",\
                "creationDate":"2021-02-05T15:10:22.434Z",\
                "imagePath":"string",\
                "source":"string",\
                "author":{"id":0,"name":"string","email":"test.email@gmail.com" },\
                "title":"string",\
                "text":"string"}\
                """;

        mockPerform(content, "/addEcoNews");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        EcoNewsForSendEmailDto message = objectMapper.readValue(content, EcoNewsForSendEmailDto.class);

        verify(emailService).sendCreatedNewsForAuthor(message);
    }

    @Test
    void sendReport() throws Exception {
        String content = """
            {\
            "categoriesDtoWithPlacesDtoMap":\
            {"additionalProp1":\
            [{"category":{"name":"string","parentCategoryId":0},\
            "name":"string"}],\
            "additionalProp2":\
            [{"category":{"name":"string","parentCategoryId":0},\
            "name":"string"}],\
            "additionalProp3":[{"category":{"name":"string","parentCategoryId":0},\
            "name":"string"}]},\
            "emailNotification":"string",\
            "subscribers":[{"email":"string","id":0,"name":"string"}]}\
            """;

        mockPerform(content, "/sendReport");

        SendReportEmailMessage message =
            new ObjectMapper().readValue(content, SendReportEmailMessage.class);

        verify(emailService).sendAddedNewPlacesReportEmail(
            message.getSubscribers(), message.getCategoriesDtoWithPlacesDtoMap(),
            message.getEmailNotification());
    }

    @Test
    void changePlaceStatus() throws Exception {
        String content = """
            {\
            "authorEmail":"string",\
            "authorFirstName":"string",\
            "placeName":"string",\
            "placeStatus":"string"\
            }\
            """;

        mockPerform(content, "/changePlaceStatus");

        SendChangePlaceStatusEmailMessage message =
            new ObjectMapper().readValue(content, SendChangePlaceStatusEmailMessage.class);

        verify(emailService).sendChangePlaceStatusEmail(
            message.getAuthorFirstName(), message.getPlaceName(),
            message.getPlaceStatus(), message.getAuthorEmail());
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
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/habitAssign/notification")
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
    void sendUserReceivedCommentNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserReceivedCommentMessage message = UserReceivedCommentMessage.builder()
            .receiverEmail("receiver@example.com")
            .receiverName("receiver")
            .elementName("event")
            .commentText("test")
            .authorName("test")
            .commentedElementId(1L)
            .language("en")
            .baseLink("testLink")
            .creationDate(LocalDateTime.now())
            .build();
        String content = objectMapper.writeValueAsString(message);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/userReceivedComment/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void sendUserReceivedCommentReplyNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserReceivedCommentReplyMessage message = UserReceivedCommentReplyMessage.builder()
            .receiverEmail("receiver@example.com")
            .receiverName("receiver")
            .elementName("event")
            .commentText("test")
            .authorName("test")
            .baseLink("testLink")
            .commentedElementId(1L)
            .language("en")
            .parentCommentAuthorName("parent")
            .parentCommentText("parentText")
            .parentCommentCreationDate(LocalDateTime.now())
            .creationDate(LocalDateTime.now())
            .build();
        String content = objectMapper.writeValueAsString(message);
        mockMvc.perform(MockMvcRequestBuilders.post(LINK + "/userReceivedCommentReply/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

}
