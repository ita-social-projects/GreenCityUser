package greencity.security.controller;

import greencity.ModelUtils;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
class ManagementSecurityControllerTest {
    private static final String LINK = "/management";
    private MockMvc mockMvc;

    @InjectMocks
    private ManagementSecurityController controller;

    @Mock
    private OwnSecurityService ownSecurityService;

    @Mock
    private VerifyEmailService verifyEmailService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/core");
        viewResolver.setSuffix(".html");
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers()
            .build();
    }

    @Test
    void loginPage() throws Exception {
        mockMvc.perform(get(LINK + "/management/login"))
            .andDo(print())
            .andExpect(view().name("management_login"));
    }

    @Test
    void signIn() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"password\": \"string\"\n" +
            "}";

        mockMvc.perform(post(LINK + "/management/login"))
            .andExpect(status().is3xxRedirection());

//        OwnSignInDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignInDto.class);
//        verify(ownSecurityService).signIn(dto);
    }
}
