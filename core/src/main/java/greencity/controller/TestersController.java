package greencity.controller;

import greencity.dto.security.UnblockTestersAccountDto;
import greencity.security.service.TestersUnblockAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/testers")
public class TestersController {
    private final TestersUnblockAccountService testersUnblockAccountService;

    /**
     * Endpoint for unblocking user account. This endpoint takes
     * {@link UnblockTestersAccountDto} as request body and unblocks user account by
     * the provided token.
     *
     * @param dto the data transfer object containing token for unblocking user
     *            account
     * @return 200 status and "Account unblocked" message if unblocking is
     *         successful, 400 status if bad request, 401 status if unauthorized
     */
    @Operation(summary = "Unblock user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account unblocked"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/unblockAccount")
    public ResponseEntity<Object> unblockAccount(@RequestBody UnblockTestersAccountDto dto) {
        testersUnblockAccountService.unblockAccount(dto);
        return ResponseEntity.ok("Account unblocked");
    }
}
