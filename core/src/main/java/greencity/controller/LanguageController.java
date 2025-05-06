package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.language.LanguageVO;
import greencity.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/lang")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    /**
     * Method to get all languages as {@link LanguageVO}.
     *
     * @return {@link List} of {@link LanguageVO}
     */
    @Operation(summary = "Find all languages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public List<LanguageVO> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    /**
     * Find language {@link LanguageVO} by code.
     *
     * @param code language code
     * @return language {@link LanguageVO}
     */
    @Operation(summary = "Find language by code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/codes/{code}")
    public LanguageVO findLanguageByCode(@PathVariable String code) {
        return languageService.findLanguageByCode(code);
    }

    /**
     * Method to get all language codes.
     *
     * @return {@link List} of {@link String} language codes
     */
    @Operation(summary = "Find all language codes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/codes")
    public List<String> findAllLanguageCodes() {
        return languageService.findAllLanguageCodes();
    }
}
