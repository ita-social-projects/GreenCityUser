package greencity.security.controller;

import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.TestersSignInRequest;
import greencity.security.service.OwnSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/testers")
public class TestersSecurityController {
    private final OwnSecurityService ownSecurityService;

    /**
     * The endpoint that allows testers to sign in without captcha token using their
     * credentials.
     *
     * @param request a {@link TestersSignInRequest} containing sign-in information
     *                for testers.
     * @return {@link ResponseEntity} containing {@link SuccessSignInDto} if sign-in
     *         is successful.
     */
    @Operation(summary = "Testers sign in endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = SuccessSignInDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/sign-in")
    public ResponseEntity<SuccessSignInDto> signIn(@RequestBody @Valid TestersSignInRequest request) {
        return ResponseEntity.ok().body(ownSecurityService.testersSignIn(request));
    }
}
