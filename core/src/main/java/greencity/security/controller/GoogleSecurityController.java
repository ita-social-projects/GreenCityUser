package greencity.security.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import static greencity.constant.ErrorMessage.BAD_GOOGLE_TOKEN;
import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.service.GoogleSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * Method that provide authenticate with Google token.
     *
     * @param token {@link String} - google token
     * @return {@link SuccessSignInDto} if token valid
     */
    @Operation(summary = "Make authentication by Google")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = SuccessSignInDto.class))),
        @ApiResponse(responseCode = "400", description = BAD_GOOGLE_TOKEN)
    })
    @GetMapping
    @ApiLocale
    public SuccessSignInDto authenticate(@RequestParam @NotBlank String token,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return googleSecurityService.authenticate(token, locale.getLanguage());
    }
}
