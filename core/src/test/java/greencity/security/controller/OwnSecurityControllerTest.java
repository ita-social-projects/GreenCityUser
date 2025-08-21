package greencity.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.authorities.AuthorityCategoryDto;
import greencity.dto.authorities.AuthorityDto;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.SetPasswordDto;
import greencity.security.dto.ownsecurity.UnblockAccountDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.service.AuthorityService;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OwnSecurityControllerTest {
    private static final String OWN_SECURITY_LINK = "/ownSecurity";
    private MockMvc mockMvc;

    @InjectMocks
    private OwnSecurityController ownSecurityController;

    @Mock
    private OwnSecurityService ownSecurityService;

    @Mock
    private VerifyEmailService verifyEmailService;

    @Mock
    AuthorityService authorityService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    String email = "test@example.com";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ownSecurityController)
            .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
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

        mockMvc.perform(post(OWN_SECURITY_LINK + "/signUp?lang=en")
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

        mockMvc.perform(post(OWN_SECURITY_LINK + "/sign-up-employee?lang=en")
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

        mockMvc.perform(post(OWN_SECURITY_LINK + "/signIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        OwnSignInDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignInDto.class);
        verify(ownSecurityService).signIn(dto);
    }

    @Test
    void verifyEmailTest() throws Exception {
        mockMvc.perform(get(OWN_SECURITY_LINK + "/verifyEmail")
            .param("token", "12345")
            .param("user_id", String.valueOf(1L)))
            .andExpect(status().isOk());

        verify(verifyEmailService).verifyByToken(1L, "12345");
    }

    @Test
    void updateAccessTokenTest() throws Exception {
        mockMvc.perform(get(OWN_SECURITY_LINK + "/updateAccessToken")
            .param("refreshToken", "12345"))
            .andExpect(status().isOk());

        verify(ownSecurityService).updateAccessTokens("12345");
    }

    @Test
    void restoreTest() throws Exception {
        mockMvc.perform(get(OWN_SECURITY_LINK + "/restorePassword")
            .param("email", "test@mail.com")
            .param("lang", "en"))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).sendPasswordRecoveryEmailTo("test@mail.com", false);
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

        mockMvc.perform(post(OWN_SECURITY_LINK + "/updatePassword")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).updatePasswordUsingToken(form);
    }

    @Test
    @SneakyThrows
    void setPassword() {
        String content = """
            {
              "password": "String123=",
              "confirmPassword": "String123="
            }\
            """;

        mockMvc.perform(post(OWN_SECURITY_LINK + "/set-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        SetPasswordDto dto = ModelUtils.getObjectMapper().readValue(content, SetPasswordDto.class);
        verify(ownSecurityService).setPassword(dto, email);
    }

    @Test
    @SneakyThrows
    void hasPassword() {
        mockMvc.perform(get(OWN_SECURITY_LINK + "/password-status"))
            .andExpect(status().isOk());

        verify(ownSecurityService).hasPassword(email);
    }

    @Test
    void updatePasswordTest() throws Exception {
        String content = """
            {
              "confirmPassword": "String123=",
              "password": "String124="
            }\
            """;

        mockMvc.perform(put(OWN_SECURITY_LINK + "/changePassword")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        UpdatePasswordDto dto =
            ModelUtils.getObjectMapper().readValue(content, UpdatePasswordDto.class);

        verify(ownSecurityService).updateCurrentPassword(dto, email);
    }

    @Test
    void getAuthoritiesByCategoryTest() throws Exception {
        Long categoryId = 1L;
        AuthorityDto authorityDto = AuthorityDto.builder()
            .name("EDIT_ORDER")
            .descriptionEn("Edit orders")
            .descriptionUk("Редагувати замовлення")
            .build();

        when(authorityService.getAuthoritiesByCategory(categoryId)).thenReturn(List.of(authorityDto));

        mockMvc.perform(get(OWN_SECURITY_LINK + "/authorities/by-category")
            .param("categoryId", String.valueOf(categoryId))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("EDIT_ORDER"));

        verify(authorityService).getAuthoritiesByCategory(categoryId);
    }

    @Test
    void getAllAuthorityCategoriesTest() throws Exception {
        AuthorityCategoryDto categoryDto = AuthorityCategoryDto.builder()
            .id(1L)
            .nameEn("Clients")
            .nameUk("Клієнти")
            .build();

        when(authorityService.getAllAuthorityCategories()).thenReturn(List.of(categoryDto));

        mockMvc.perform(get(OWN_SECURITY_LINK + "/authorities/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nameEn").value("Clients"))
            .andExpect(jsonPath("$[0].nameUk").value("Клієнти"));

        verify(authorityService).getAllAuthorityCategories();
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete(OWN_SECURITY_LINK + "/user"))
            .andExpect(status().isOk());

        verify(ownSecurityService).deleteUserByEmail(email);
    }

    @Test
    void unblockUserTest() throws Exception {
        UnblockAccountDto accountDto = new UnblockAccountDto("token");

        doNothing().when(ownSecurityService).unblockAccount(accountDto.token());

        mockMvc.perform(post(OWN_SECURITY_LINK + "/unblockAccount")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(accountDto)))
            .andExpect(status().isOk());

        verify(ownSecurityService, times(1)).unblockAccount(accountDto.token());
    }
}
