package greencity.security.controller;

import static greencity.ModelUtils.TEST_USER_VO;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
class ManagementSecurityControllerTest {
    private static final String LINK = "/management";
    private static final String CAPTCHA_TOKEN = "token";
    private MockMvc mockMvc;
    private SuccessSignInDto successDto;

    @InjectMocks
    private ManagementSecurityController controller;

    @Mock
    private OwnSecurityService ownSecurityService;

    @Mock
    private UserService userService;

    @Mock
    BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/core");
        viewResolver.setSuffix(".html");
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers(viewResolver)
            .build();
        this.successDto = SuccessSignInDto.builder()
            .accessToken("ejqwsadsdadq")
            .userId(1L)
            .build();
    }

    @Test
    void loginPage() throws Exception {
        mockMvc.perform(get(LINK + "/login"))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void loginPageInvalidDto() throws Exception {
        mockMvc.perform(post(LINK + "/login"))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void signIn() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("test@gmail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenReturn(successDto);
        when(userService.findAdminById(successDto.getUserId())).thenReturn(TEST_USER_VO);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void signInWrongEmail() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("tesssweqwest@gmail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenThrow(WrongEmailException.class);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void signInWrongPassword() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("tesssweqwest@gmail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenThrow(WrongPasswordException.class);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void signInEmailNotVerified() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("tesssweqwest@gmail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenThrow(EmailNotVerified.class);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void signInUserDeactivated() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("tesssweqwest@gmail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenThrow(UserDeactivatedException.class);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(view().name("core/management_login"));
    }

    @Test
    void signInUserDoNotHaveAuthorities() throws Exception {
        OwnSignInDto dto = new OwnSignInDto("test@mail.com", "Vovk@1998", CAPTCHA_TOKEN);
        when(ownSecurityService.signIn(any())).thenReturn(successDto);
        when(userService.findAdminById(1L)).thenThrow(LowRoleLevelException.class);

        mockMvc.perform(post(LINK + "/login")
            .flashAttr("signInForm", dto))
            .andExpect(view().name("core/management_login"));
    }
}
