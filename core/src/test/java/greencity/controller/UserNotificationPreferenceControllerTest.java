package greencity.controller;

import static greencity.enums.EmailPreference.SYSTEM;
import static greencity.enums.EmailPreferencePeriodicity.DAILY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.service.SocialNetworkImageService;
import greencity.service.UserNotificationPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserNotificationPreferenceControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private UserNotificationPreferenceService userNotificationPreferenceService;
    @InjectMocks
    private UserNotificationPreferenceController userNotificationPreferenceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userNotificationPreferenceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void existsByUserIdAndEmailPreferenceAndPeriodicityTrueTest() throws Exception {
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(1L, SYSTEM, DAILY);
        when(userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(
            emailPreferenceDto)).thenReturn(true);
        mockMvc.perform(post("/user-notification-preference/search").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(emailPreferenceDto)))
            .andExpect(status().isOk()).andExpect(content().string("true"));
    }

    @Test
    void existsByUserIdAndEmailPreferenceAndPeriodicityFalseTest() throws Exception {
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(1L, SYSTEM, DAILY);
        when(userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(
            emailPreferenceDto)).thenReturn(false);
        mockMvc.perform(post("/user-notification-preference/search").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(emailPreferenceDto)))
            .andExpect(status().isOk()).andExpect(content().string("false"));
    }
}
