package greencity.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.TestersSignInRequest;
import greencity.security.service.OwnSecurityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestersSecurityControllerTest {
    private static final String LINK = "/api/testers";

    private MockMvc mockMvc;
    private TestersSignInRequest request;
    private ObjectMapper objectMapper;

    @InjectMocks
    private TestersSecurityController testersSecurityController;
    @Mock
    private OwnSecurityServiceImpl ownSecurityService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(testersSecurityController)
            .defaultRequest(MockMvcRequestBuilders.get("/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .build();
        this.request = TestersSignInRequest.builder()
            .email("test@email.com")
            .password("password")
            .secretKey("secretKey")
            .build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void signInTest() throws Exception {
        SuccessSignInDto successSignInDto = SuccessSignInDto.builder()
            .accessToken("sample-jwt-token")
            .userId(12345L)
            .build();

        when(ownSecurityService.testersSignIn(request)).thenReturn(successSignInDto);

        mockMvc.perform(post(LINK + "/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("sample-jwt-token"))
            .andExpect(jsonPath("$.userId").value("12345"));

        verify(ownSecurityService).testersSignIn(request);
    }
}
