package greencity.security.controller;

import greencity.ModelUtils;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.SetPasswordDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import java.security.Principal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        String content = """
            {
              "email": "test@mail.com",
              "name": "String",
              "password": "String123=",
              "isUbs": false
            }\
            """;

        mockMvc.perform(post(LINK + "/signUp?lang=en")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        OwnSignUpDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignUpDto.class);
        verify(ownSecurityService).signUp(dto, "en");
    }

    @Test
    void singUpEmployeeTest() throws Exception {
        String content = """
            {
              "email": "test@mail.com",
              "name": "String",
              "isUbs": true
            }\
            """;

        mockMvc.perform(post(LINK + "/sign-up-employee?lang=en")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        EmployeeSignUpDto dto = ModelUtils.getObjectMapper().readValue(content, EmployeeSignUpDto.class);
        verify(ownSecurityService).signUpEmployee(dto, "en");
    }

    @Test
    void signInTest() throws Exception {
        String content = """
            {
              "email": "test@mail.com",
              "password": "String-123"
            }\
            """;

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
        String content = """
            {
              "confirmPassword": "String123=",
              "password": "String124=",
              "token": "12345",
              "isUbs": "false"
            }\
            """;

        OwnRestoreDto form = new OwnRestoreDto("String124=", "String123=", "12345", false);

        mockMvc.perform(post(LINK + "/updatePassword")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).updatePasswordUsingToken(form);
    }

    // @Test
    void updatePasswordTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        String content = """
            {
              "confirmPassword": "String123=",
              "password": "String124="
            }\
            """;

        mockMvc.perform(put(LINK + "/changePassword")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        UpdatePasswordDto dto =
            ModelUtils.getObjectMapper().readValue(content, UpdatePasswordDto.class);

        verify(ownSecurityService).updateCurrentPassword(dto, "test@mail.com");
    }

    // @Test
    @SneakyThrows
    void hasPassword() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        mockMvc.perform(get(LINK + "/password-status")
            .principal(principal))
            .andExpect(status().isOk());

        verify(ownSecurityService).hasPassword("test@mail.com");
    }

    // @Test
    @SneakyThrows
    void setPassword() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        String content = """
            {
              "password": "String123=",
              "confirmPassword": "String123="
            }\
            """;

        SetPasswordDto dto = ModelUtils.getObjectMapper().readValue(content, SetPasswordDto.class);

        mockMvc.perform(post(LINK + "/set-password")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        verify(ownSecurityService).setPassword(dto, "test@mail.com");
    }
}
