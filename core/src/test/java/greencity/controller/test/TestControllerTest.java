package greencity.controller.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setUp() {
        testController.allowedOrigins = new String[] {"http://localhost:8080"};
        this.mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void testGetAllowedOrigins() throws Exception {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk());
    }
}
