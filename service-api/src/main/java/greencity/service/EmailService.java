package greencity.service;

import greencity.dto.category.CategoryDto;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.ScheduledEmailMessage;
import greencity.message.UserTaggedInCommentMessage;
import java.util.List;
import java.util.Map;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */

public interface EmailService {
    /**
     * Method for sending notification to users who subscribed for updates about
     * added new places.
     *
     * @param subscribers          list of users for receiving email.
     * @param categoriesWithPlaces map with {@link CategoryDto} and
     *                             {@link PlaceNotificationDto}`s which were
     *                             created.
     * @param notification         type of notification.
     */
    void sendAddedNewPlacesReportEmail(List<PlaceAuthorDto> subscribers,
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlaces,
        String notification);

    /**
     * Method for sending interesting news for subscribers.
     *
     * @param interestingEcoNews - includes all information about ecoNews and
     *                           subscribers.
     */
    void sendInterestingEcoNews(InterestingEcoNewsDto interestingEcoNews);

    /**
     * Method for sending simple notification to {@code User} about change status.
     *
     * @param authorFirstName place author's first name.
     * @param placeName       name of a place.
     * @param placeStatus     updated status of a place.
     * @param authorEmail     author's email.
     */
    void sendChangePlaceStatusEmail(String authorFirstName, String placeName,
        String placeStatus, String authorEmail);

    /**
     * Method for sending verification email to {@link User}.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token current user.
     */
    void sendVerificationEmail(Long userId, String userName, String userEmail, String token, String language,
        boolean isUbs);

    /**
     * Method for sending user approval email to User, when Admin adds the User from
     * admin panel.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token.
     */
    void sendApprovalEmail(Long userId, String userName, String userEmail, String token);

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId       the user id is used for recovery link building.
     * @param userFistName user first name is used in email model constants.
     * @param userEmail    user email which will be used for sending recovery
     *                     letter.
     * @param token        password recovery token.
     */
    void sendRestoreEmail(Long userId, String userFistName, String userEmail, String token, String language,
        boolean isUbs);

    /**
     * Sends email notification about not marked habits during 3 last days.
     *
     * @param name  user name is used in email letter.
     * @param email letter is sent to this email.
     */
    void sendHabitNotification(String name, String email);

    /**
     * Method for sending reasons of deactivating the user.
     *
     * @param userDeactivationDto - includes all information about the User.
     */
    void sendReasonOfDeactivation(UserDeactivationReasonDto userDeactivationDto);

    /**
     * Method for send message of activation user.
     *
     * @param userActivationDto - includes all information about the User.
     */
    void sendMessageOfActivation(UserActivationDto userActivationDto);

    /**
     * Method for send violation to user.
     * 
     * @param dto {@link UserViolationMailDto}-includes all information about
     *            Violation.
     * @author Zakhar Veremchuk.
     */
    void sendUserViolationEmail(UserViolationMailDto dto);

    /**
     * Method for send information about success restoring password.
     *
     * @param email    letter is sent to this email.
     * @param language language which will be used in letter.
     *
     * @author Pavlo Hural.
     */
    void sendSuccessRestorePasswordByEmail(String email, String language, String userName, boolean isUbs);

    /**
     * Sends email message to create new password for employee after signUp.
     *
     * @param employeeId       {@link Long} the user id is used for recovery link
     *                         building
     * @param employeeFistName {@link String} user first name is used in email model
     *                         constants
     * @param employeeEmail    {@link String} user email which will be used for
     *                         sending recovery letter
     * @param language         {@link String} language code used for email
     *                         notification
     * @param token            {@link String} token for password save(restoration)
     *
     * @author Olena Sotnik
     */
    void sendCreateNewPasswordForEmployee(Long employeeId, String employeeFistName, String employeeEmail, String token,
        String language, boolean isUbs);

    /**
     * Sends an email notification user that received scheduled message
     * {@link ScheduledEmailMessage}.
     *
     * @param message {@link ScheduledEmailMessage}
     * @author Dmytro Dmytruk
     */
    void sendScheduledNotificationEmail(ScheduledEmailMessage message);
}
