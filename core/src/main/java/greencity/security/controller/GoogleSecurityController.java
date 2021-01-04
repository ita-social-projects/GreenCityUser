package greencity.security.controller;

import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.service.GoogleSecurityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

import static greencity.constant.ErrorMessage.BAD_GOOGLE_TOKEN;

/**
 * Controller that provide google security logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@RestController
@RequestMapping("/googleSecurity")
@Validated
public class GoogleSecurityController {
    private final GoogleSecurityService googleSecurityService;

    /**
     * Constructor.
     *
     * @param googleSecurityService {@link GoogleSecurityService}
     */
    @Autowired
    public GoogleSecurityController(GoogleSecurityService googleSecurityService) {
        this.googleSecurityService = googleSecurityService;
    }

    /**
     * Method that provide authenticate with google token.
     *
     * @param idToken {@link String} - google idToken
     * @return {@link SuccessSignInDto} if token valid
     */
    @ApiOperation("Make authentication by Google")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = SuccessSignInDto.class),
        @ApiResponse(code = 400, message = BAD_GOOGLE_TOKEN)
    })
    @GetMapping
    public SuccessSignInDto authenticate(@RequestParam @NotBlank String idToken) {
        return googleSecurityService.authenticate(idToken);
    }
}
