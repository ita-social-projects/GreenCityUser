package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserTelegramFeedbackDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.*;
import greencity.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    /**
     * Method for sending interesting news for subscribers.
     *
     * @param message - object with all necessary data for sending email
     */
    @PostMapping("/sendInterestingEcoNews")
    public ResponseEntity<Object> sendInterestingEcoNews(@RequestBody InterestingEcoNewsDto message) {
        emailService.sendInterestingEcoNews(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for sending notification to users who subscribed for updates about
     * added new places.
     *
     * @param message - object with all necessary data for sending email
     */
    @PostMapping("/sendReport")
    public ResponseEntity<Object> sendReport(@RequestBody SendReportEmailMessage message) {
        emailService.sendAddedNewPlacesReportEmail(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Sends email notification about not marked habits during 3 last days.
     *
     * @param sendHabitNotification - object with all necessary data for sending
     *                              email
     */
    @PostMapping("/sendHabitNotification")
    public ResponseEntity<Object> sendHabitNotification(@RequestBody SendHabitNotification sendHabitNotification) {
        emailService.sendHabitNotification(sendHabitNotification.getName(), sendHabitNotification.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Sends email notification about violation to user on email.
     *
     * @param dto {@link UserViolationMailDto} - object with all necessary data for
     *            sending email.
     */
    @PostMapping("/sendUserViolation")
    public ResponseEntity<Object> sendUserViolation(@RequestBody UserViolationMailDto dto) {
        emailService.sendUserViolationEmail(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Sends scheduled email notification to user.
     *
     * @param message {@link ScheduledEmailMessage} - object with all necessary data
     *                for sending notification via email.
     */
    @Operation(summary = "Send scheduled email notification to user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/scheduled/notification")
    public ResponseEntity<Void> sendScheduledNotification(@RequestBody ScheduledEmailMessage message) {
        emailService.sendScheduledNotificationEmail(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Send email notification to manager about green office request.
     *
     * @param message {@link ScheduledEmailMessage} - object with all necessary data
     *                for sending notification via email.
     */
    @Operation(summary = "Send email notification to manager about green office request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.NOT_FOUND),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/greenoffice/notification")
    public ResponseEntity<Void> sendGreenOfficeRequestNotification(@RequestBody ScheduledEmailMessage message) {
        emailService.sendGreenOfficeRequestEmailToManager(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for sending an email notification about the status change of a place
     * to the user.
     *
     * @param dto Object containing the necessary information for sending the status
     *            change notification email. The object includes: - userName: The
     *            name of the user. - userEmail: The email of the user who will
     *            receive the notification. - placeName: The name of the place whose
     *            status has been changed. - newStatus: The new status of the place.
     *
     * @return ResponseEntity with HTTP status 200 OK if the email was successfully
     *         sent. If any error occurs, an appropriate error response will be
     *         returned.
     */
    @Operation(summary = "Send email notification to user if place status changed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/sendPlaceStatusChange")
    public ResponseEntity<Object> sendPlaceStatusChange(@RequestBody PlaceStatusChangeDto dto) {
        emailService.sendPlaceStatusChangeNotification(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Send an email with user feedback received from the Telegram bot.
     *
     * @param dto {@link UserTelegramFeedbackDto} - object containing the feedback
     *            details: chat ID, username, rating, optional comment and email
     *            subject.
     */
    @Operation(summary = "Send telegram user feedback to customer email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/telegram-feedback")
    public ResponseEntity<Void> sendTelegramFeedback(@RequestBody @Valid UserTelegramFeedbackDto dto) {
        emailService.sendTelegramFeedbackEmail(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Send an email with reason of deactivation.
     *
     * @param dto {@link UserDeactivationReasonDto} - object containing the
     *            deactivation details: email, name, deactivation reason and
     *            language.
     */
    @Operation(summary = "Send reason of deactivation to customer email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/sendReasonOfDeactivation")
    public ResponseEntity<Void> sendReasonOfDeactivation(@RequestBody UserDeactivationReasonDto dto) {
        emailService.sendReasonOfDeactivation(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Send an email that user account was activated.
     *
     * @param dto {@link UserActivationDto} - object containing the activation details:
     *            email, name, language.
     */
    @Operation(summary = "Send an email that user account was activated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/sendMessageOfActivation")
    public ResponseEntity<Void> sendMessageOfActivation(@RequestBody UserActivationDto dto) {
        emailService.sendMessageOfActivation(dto);
        return ResponseEntity.ok().build();
    }
}
