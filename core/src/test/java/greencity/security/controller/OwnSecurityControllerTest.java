package greencity.security.controller;

import greencity.ModelUtils;
import greencity.security.dto.ownsecurity.*;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OwnSecurityControllerTest {
    private static final String LINK = "/ownSecurity";
    private MockMvc mockMvc;

    @InjectMocks
    private OwnSecurityController ownSecurityController;

    @Mock
    private OwnSecurityService ownSecurityService;

    @Mock
    private VerifyEmailService verifyEmailService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ownSecurityController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void singUpTest() throws Exception {
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"name\": \"String\",\n" +
            "  \"password\": \"String123=\",\n" +
            "  \"isUbs\": false\n" +
            "}";

        mockMvc.perform(post(LINK + "/signUp?lang=en")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        OwnSignUpDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignUpDto.class);
        verify(ownSecurityService).signUp(dto, "en");
    }

    @Test
    void singUpEmployeeTest() throws Exception {
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"name\": \"String\",\n" +
            "  \"isUbs\": true\n" +
            "}";

        mockMvc.perform(post(LINK + "/sign-up-employee?lang=en")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        EmployeeSignUpDto dto = ModelUtils.getObjectMapper().readValue(content, EmployeeSignUpDto.class);
        verify(ownSecurityService).signUpEmployee(dto, "en");
    }

    @Test
    void signInTest() throws Exception {
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"password\": \"String-123\"\n" +
            "}";

        mockMvc.perform(post(LINK + "/signIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        OwnSignInDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignInDto.class);
        verify(ownSecurityService).signIn(dto);
    }

    @Test
    void verifyEmailTest() throws Exception {
        mockMvc.perform(get(LINK + "/verifyEmail")
            .param("token", "12345")
            .param("user_id", String.valueOf(1L)))
            .andExpect(status().isOk());

        verify(verifyEmailService).verifyByToken(1L, "12345");
    }

    @Test
    void updateAccessTokenTest() throws Exception {
        mockMvc.perform(get(LINK + "/updateAccessToken")
            .param("refreshToken", "12345"))
            .andExpect(status().isOk());

        verify(ownSecurityService).updateAccessTokens("12345");
    }

    @Test
    void restoreTest() throws Exception {
        mockMvc.perform(get(LINK + "/restorePassword")
            .param("email", "test@mail.com")
            .param("lang", "en"))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).sendPasswordRecoveryEmailTo("test@mail.com", false, "en");
    }

    @Test
    void changePasswordTest() throws Exception {
        String content = "{\n" +
            "  \"confirmPassword\": \"String123=\",\n" +
            "  \"password\": \"String124=\",\n" +
            "  \"token\": \"12345\",\n" +
            "  \"isUbs\": \"false\"\n" +
            "}";

        OwnRestoreDto form = new OwnRestoreDto("String124=", "String123=", "12345", false);

        mockMvc.perform(post(LINK + "/updatePassword")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).updatePasswordUsingToken(form);
    }

    @Test
    void updatePasswordTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        String content = "{\n" +
            "  \"confirmPassword\": \"String123=\",\n" +
            "  \"password\": \"String124=\"\n" +
            "}";

        mockMvc.perform(put(LINK + "/changePassword")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        UpdatePasswordDto dto =
            ModelUtils.getObjectMapper().readValue(content, UpdatePasswordDto.class);

        verify(ownSecurityService).updateCurrentPassword(dto, "test@mail.com");
    }

    @Test
    @SneakyThrows
    void hasPassword() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        mockMvc.perform(get(LINK + "/password-status")
            .principal(principal))
            .andExpect(status().isOk());

        verify(ownSecurityService).hasPassword("test@mail.com");
    }

    @Test
    @SneakyThrows
    void setPassword() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        String content = "{\n" +
            "  \"password\": \"String123=\",\n" +
            "  \"confirmPassword\": \"String123=\"\n" +
            "}";

        SetPasswordDto dto = ModelUtils.getObjectMapper().readValue(content, SetPasswordDto.class);

        mockMvc.perform(post(LINK + "/set-password")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        verify(ownSecurityService).setPassword(dto, "test@mail.com");
    }
}
