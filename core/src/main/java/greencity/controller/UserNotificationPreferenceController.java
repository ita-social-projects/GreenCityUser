package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.service.UserNotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/user-notification-preference")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserNotificationPreferenceController {
    private final UserNotificationPreferenceService userNotificationPreferenceService;

    /**
     * Check is user notification preference exists by params in EmailPreferenceDto.
     *
     * @return boolean of whether UserNotificationPreference exists
     */
    @Operation(summary = "Check is user notification preference exists by params in EmailPreferenceDto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/search")
    public ResponseEntity<Boolean> existsByUserIdAndEmailPreferenceAndPeriodicity(
        @RequestBody EmailPreferenceDto emailPreferenceDto) {
        return ResponseEntity.ok()
            .body(userNotificationPreferenceService.existsByUserIdAndEmailPreferenceAndPeriodicity(emailPreferenceDto));
    }
}
