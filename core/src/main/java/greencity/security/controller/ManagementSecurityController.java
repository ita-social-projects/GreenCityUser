package greencity.security.controller;

import greencity.constant.RestTemplateLinks;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.service.OwnSecurityService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("/management")
public class ManagementSecurityController {
    private final OwnSecurityService service;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;

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
        model.addAttribute("signInForm", new OwnSignInDto());
        return "core/management_login";
    }

    /**
     * Redirects user to management page with access toket set in cookies.
     *
     * @param dto - {@link OwnSignInDto} - form filled with log in data.
     * @return View template path {@link String}.
     */
    @PostMapping("/login")
    public String signIn(@Valid @ModelAttribute("signInForm") OwnSignInDto dto) {
        SuccessSignInDto result = service.signIn(dto);

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(greenCityServerAddress
            + RestTemplateLinks.TOKEN).queryParam("accessToken", result.getAccessToken());
        return "redirect:" + uri.toUriString();
    }
}
