package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.security.UnblockTestersAccountDto;
import greencity.security.service.TestersUnblockAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestersControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TestersUnblockAccountService testersUnblockAccountService;

    @InjectMocks
    private TestersController testersController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testersController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUnblockAccountSuccess() throws Exception {
        UnblockTestersAccountDto dto = new UnblockTestersAccountDto("token", "email");

        doNothing().when(testersUnblockAccountService).unblockAccount(dto);

        mockMvc.perform(put("/testers/unblockAccount")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(content().string("Account unblocked"));

        verify(testersUnblockAccountService).unblockAccount(dto);
    }
}
