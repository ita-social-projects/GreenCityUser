package greencity.security.controller;

import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.service.OwnSecurityService;
import javax.validation.Valid;
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

@Controller
@ApiIgnore
@RequestMapping("/management")
public class ManagementSecurityController {
    private final OwnSecurityService service;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    private static final String signInForm = "signInForm";
    private static final String managementLoginPage = "core/management_login";

    /**
     * Constructor.
     *
     * @param service - - {@link OwnSecurityService} - service for security logic.
     */
    @Autowired
    public ManagementSecurityController(OwnSecurityService service) {
        this.service = service;
    }

    /**
     * Controller returns view for management log in.
     *
     * @param model - ModelAndView that will be configured.
     * @return View template path {@link String}.
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute(signInForm, new OwnSignInDto());
        return managementLoginPage;
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
            return managementLoginPage;
        }

        SuccessSignInDto result;
        String email = "email";
        String signInFormEmailError = "signInForm.email";
        try {
            result = service.signIn(dto);
        } catch (WrongEmailException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Неправильна пошта");
            model.addAttribute(signInForm, dto);
            return managementLoginPage;
        } catch (WrongPasswordException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Неправильний пароль");
            model.addAttribute(signInForm, dto);
            return managementLoginPage;
        } catch (EmailNotVerified e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Електронна поште не підтверджена");
            model.addAttribute(signInForm, dto);
            return managementLoginPage;
        } catch (UserDeactivatedException e) {
            bindingResult.rejectValue(email, signInFormEmailError, "Цей користувач деактивований");
            model.addAttribute(signInForm, dto);
            return managementLoginPage;
        }

        return "redirect:" + greenCityServerAddress + "/token?accessToken=" + result.getAccessToken();
    }
}