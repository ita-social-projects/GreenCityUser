package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.service.EmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    @Autowired
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
     * Method for sending notification to userss who subscribed for updates about
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
    @PostMapping("/changePlaceStatus")
    public ResponseEntity<Object> changePlaceStatus(@RequestBody SendChangePlaceStatusEmailMessage message) {
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
     * @param notification {@link NotificationDto} - object with all necessary data
     *                     for sending notification via email.
     * @param email        {@link String} - user's email.
     * @author Ann Sakhno
     */
    @ApiOperation(value = "Send notification to user via email")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/notification")
    public ResponseEntity<Object> sendUserNotification(@RequestBody NotificationDto notification,
        @RequestParam("email") String email) {
        emailService.sendNotificationByEmail(notification, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
