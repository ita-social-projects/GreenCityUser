package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.*;
import greencity.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    /**
     * Method for sending news for users who subscribed for updates.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @PostMapping("/addEcoNews")
    public ResponseEntity<Object> addEcoNews(@RequestBody EcoNewsForSendEmailDto message) {
        emailService.sendCreatedNewsForAuthor(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending notification to users who subscribed for updates about
     * added new places.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @PostMapping("/sendReport")
    public ResponseEntity<Object> sendReport(@RequestBody SendReportEmailMessage message) {
        emailService.sendAddedNewPlacesReportEmail(message.getSubscribers(), message.getCategoriesDtoWithPlacesDtoMap(),
            message.getEmailNotification());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending simple notification to {@code User} about status change.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @PostMapping("/changePlaceStatus")
    public ResponseEntity<Object> changePlaceStatus(@RequestBody @Valid SendChangePlaceStatusEmailMessage message) {
        emailService.sendChangePlaceStatusEmail(message.getAuthorFirstName(), message.getPlaceName(),
            message.getPlaceStatus(), message.getAuthorEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends email notification about not marked habits during 3 last days.
     *
     * @param sendHabitNotification - object with all necessary data for sending
     *                              email
     * @author Taras Kavkalo
     */
    @PostMapping("/sendHabitNotification")
    public ResponseEntity<Object> sendHabitNotification(@RequestBody SendHabitNotification sendHabitNotification) {
        emailService.sendHabitNotification(sendHabitNotification.getName(), sendHabitNotification.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends email notification about violation to user on email.
     *
     * @param dto {@link UserViolationMailDto} - object with all necessary data for
     *            sending email.
     * @author Zakhar Veremchuk
     */
    @PostMapping("/sendUserViolation")
    public ResponseEntity<Object> sendUserViolation(@RequestBody UserViolationMailDto dto) {
        emailService.sendUserViolationEmail(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends notification to user on email.
     *
     * @param notification {@link GeneralEmailMessage} - object with all necessary
     *                     data for sending notification via email.
     * @author Yurii Midianyi
     */
    @Operation(summary = "Send general email notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @PostMapping("/general/notification")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> sendEmailNotification(@RequestBody GeneralEmailMessage notification) {
        emailService.sendEmailNotification(notification);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends habit assign email notification.
     *
     * @param message {@link HabitAssignNotificationMessage} - object with all
     *                necessary data for sending notification via email.
     */
    @Operation(summary = "Send habit assign email notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/habitAssign/notification")
    public ResponseEntity<Void> sendHabitAssignNotification(
        @RequestBody @Valid HabitAssignNotificationMessage message) {
        emailService.sendHabitAssignNotificationEmail(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Sends email notification to tagged user in comment.
     *
     * @param message {@link UserTaggedInCommentMessage} - object with all necessary
     *                data for sending notification via email.
     */
    @Operation(summary = "Send email notification to tagged user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/taggedUserInComment/notification")
    public ResponseEntity<Void> sendTaggedUserInCommentNotification(
        @RequestBody @Valid UserTaggedInCommentMessage message) {
        emailService.sendTaggedUserInCommentNotificationEmail(message);
        return ResponseEntity.ok().build();
    }

    /**
     * Sends scheduled email notification to user.
     *
     * @param message {@link ScheduledEmailMessage} - object with all necessary data
     *                for sending notification via email.
     * @author Dmytro Dmytruk
     */
    @Operation(summary = "Send scheduled email notification to user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/scheduled/notification")
    public ResponseEntity<Void> sendScheduledNotification(
        @RequestBody @Valid ScheduledEmailMessage message) {
        emailService.sendScheduledNotificationEmail(message);
        return ResponseEntity.ok().build();
    }
}
