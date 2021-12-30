package greencity.security.controller;

import greencity.exception.exceptions.*;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Controller
@ApiIgnore
@RequestMapping("/management")
public class ManagementSecurityController {
    private final OwnSecurityService service;
    private final UserService userService;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    private static final String SIGN_IN_FORM = "signInForm";
    private static final String MANAGEMENT_LOGIN_PAGE = "core/management_login";

    /**
     * Constructor.
     *
     * @param service     - - {@link OwnSecurityService} - service for security
     *                    logic.
     * @param userService - {@link UserService} - service for User manipulations.
     */
    @Autowired
    public ManagementSecurityController(OwnSecurityService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    /**
     * Controller returns view for management log in.
     *
     * @param model - ModelAndView that will be configured.
     * @return View template path {@link String}.
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute(SIGN_IN_FORM, new OwnSignInDto());
        return MANAGEMENT_LOGIN_PAGE;
    }

    /**
     * Redirects user to management page with access toket set in cookies.
     *
     * @param dto - {@link OwnSignInDto} - form filled with log in data.
     * @return View template path {@link String}.
     */
    @PostMapping("/login")
    public String signIn(@Valid @ModelAttribute("signInForm") OwnSignInDto dto,
        BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return MANAGEMENT_LOGIN_PAGE;
        }

        SuccessSignInDto result;
        String email = "email";
        String signInFormEmailError = "signInForm.email";
        try {
            result = service.signIn(dto);

            userService.findAdminById(result.getUserId());
        } catch (LowRoleLevelException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "У вас немає прав адміністратора");
            model.addAttribute(SIGN_IN_FORM, dto);
            return MANAGEMENT_LOGIN_PAGE;
        } catch (WrongEmailException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Неправильна пошта");
            model.addAttribute(SIGN_IN_FORM, dto);
            return MANAGEMENT_LOGIN_PAGE;
        } catch (WrongPasswordException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Неправильний пароль");
            model.addAttribute(SIGN_IN_FORM, dto);
            return MANAGEMENT_LOGIN_PAGE;
        } catch (EmailNotVerified e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Електронна поште не підтверджена");
            model.addAttribute(SIGN_IN_FORM, dto);
            return MANAGEMENT_LOGIN_PAGE;
        } catch (UserDeactivatedException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Цей користувач деактивований");
            model.addAttribute(SIGN_IN_FORM, dto);
            return MANAGEMENT_LOGIN_PAGE;
        }

        return "redirect:" + greenCityServerAddress + "/token?accessToken=" + result.getAccessToken();
    }
}
