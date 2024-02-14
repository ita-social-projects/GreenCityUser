package greencity.security.controller;

import greencity.security.service.GoogleSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GoogleSecurityControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private GoogleSecurityController googleSecurityController;

    @Mock
    private GoogleSecurityService googleSecurityService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(googleSecurityController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void authenticateTest() throws Exception {
        mockMvc.perform(get("/googleSecurity")
            .param("idToken", "almostSecretToken")
            .param("lang", "en"))
            .andExpect(status().isOk());
        verify(googleSecurityService).authenticate("almostSecretToken", "en");
    }
}
