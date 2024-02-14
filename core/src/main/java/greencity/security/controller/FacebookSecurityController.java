package greencity.security.controller;

import static greencity.constant.ErrorMessage.BAD_FACEBOOK_TOKEN;
import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.service.FacebookSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that provide google security logic.
 *
 * @author Oleh Yurchuk
 * @version 1.0
 */
@RestController
@RequestMapping("/facebookSecurity")
public class FacebookSecurityController {
    private final FacebookSecurityService facebookSecurityService;

    /**
     * Constructor.
     *
     * @param facebookSecurityService {@link FacebookSecurityService}
     */
    @Autowired
    public FacebookSecurityController(FacebookSecurityService facebookSecurityService) {
        this.facebookSecurityService = facebookSecurityService;
    }

    /**
     * Method that generate facebook authorization url.
     *
     * @return {@link String} facebook auth url
     */
    @Operation(summary = "Generate Facebook Authorization URL")
    @GetMapping("/generateFacebookAuthorizeURL")
    public String generateFacebookAuthorizeURL() {
        return facebookSecurityService.generateFacebookAuthorizeURL();
    }

    /**
     * Method that provide authenticate with facebook token.
     *
     * @param code {@link String} - facebook token.
     * @return {@link SuccessSignInDto} if token valid
     */
    @Operation(summary = "Make authentication by Facebook")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = SuccessSignInDto.class))),
        @ApiResponse(responseCode = "400", description = BAD_FACEBOOK_TOKEN)
    })
    @GetMapping("/facebook")
    public SuccessSignInDto generateFacebookAccessToken(@RequestParam("code") String code) {
        return facebookSecurityService.generateFacebookAccessToken(code);
    }
}
