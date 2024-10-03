package greencity;

import greencity.dto.user.UserDeactivationReasonDto;
import greencity.service.EmailService;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserApplication {
    /**
     * Main method of SpringBoot app.
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UserApplication.class, args);
        EmailService emailService = context.getBean(EmailService.class);

        UserDeactivationReasonDto userDeactivationReasonDto = UserDeactivationReasonDto.builder()
            .email("rozhkoilia5533@gmail.com")
            .name("Ilia")
            .lang("ua")
            .deactivationReason("You racist!")
            .build();
        emailService.sendReasonOfDeactivation(userDeactivationReasonDto);

        userDeactivationReasonDto.setLang("en");
        emailService.sendReasonOfDeactivation(userDeactivationReasonDto);


//        emailService.sendApprovalEmail(1L, "Ilia", "rozhkoilia5533@gmail.com", "1234");


//        UserActivationDto userActivationDto = UserActivationDto.builder()
//            .email("rozhkoilia5533@gmail.com")
//            .name("Ilia")
//            .lang("ua")
//            .build();
//        emailService.sendMessageOfActivation(userActivationDto);
//
//        userActivationDto.setLang("en");
//        emailService.sendMessageOfActivation(userActivationDto);


//        emailService.sendSuccessRestorePasswordByEmail("rozhkoilia5533@gmail.com", "ua", "Ilia", true);
//        emailService.sendSuccessRestorePasswordByEmail("rozhkoilia5533@gmail.com", "en", "Ilia", false);


//        ScheduledEmailMessage scheduledEmailMessage = ScheduledEmailMessage.builder()
//            .username("Ilia")
//            .email("rozhkoilia5533@gmail.com")
//            .baseLink("https://www.greencity.cx.ua/#/profile/2174/notifications")
//            .subject("You received a like.")
//            .body("Maks liked your comment 'Hi guy' to EcoNews 'Best eco news'.")
//            .language("en")
//            .build();
//        emailService.sendScheduledNotificationEmail(scheduledEmailMessage);
//
//        ScheduledEmailMessage scheduledEmailMessage1 = ScheduledEmailMessage.builder()
//            .username("Ilia")
//            .email("rozhkoilia5533@gmail.com")
//            .baseLink("https://example.com")
//            .subject("Ви отримали лайк")
//            .body("Від Maks отримано лайк на ваш коментар 'What shit do you write?' до еко новини 'Shit. Shit everywhere'.")
//            .language("ua")
//            .build();
//        emailService.sendScheduledNotificationEmail(scheduledEmailMessage1);


//        emailService.sendRestoreEmail(2174L, "Ilia", "rozhkoilia5533@gmail.com", "1234", "ua", true);
//        emailService.sendRestoreEmail(2174L, "Ilia", "rozhkoilia5533@gmail.com", "1234", "en", false);


//        SendReportEmailMessage sendReportEmailMessage = SendReportEmailMessage.builder()
//            .subscribers(List.of(
//                SubscriberDto.builder()
//                    .email("rozhkoilia5533@gmail.com")
//                    .name("Ilia")
//                    .build()
//            ))
//            .categoriesDtoWithPlacesDtoMap(Map.of(
//                CategoryDto.builder()
//                    .name("Cycling routes")
//                    .build(),
//                List.of(
//                    PlaceNotificationDto.builder()
//                        .name("Central Park")
//                        .category(CategoryDto.builder()
//                            .name("Cycling routes")
//                            .build())
//                        .build(),
//                    PlaceNotificationDto.builder()
//                        .name("MTB Route")
//                        .category(CategoryDto.builder()
//                            .name("Cycling routes")
//                            .build())
//                        .build()
//                ),
//                CategoryDto.builder()
//                    .name("Bike rentals")
//                    .build(),
//                List.of(
//                    PlaceNotificationDto.builder()
//                        .name("New Bikes")
//                        .category(CategoryDto.builder()
//                            .name("Bike rentals")
//                            .build())
//                        .build()
//                )
//            ))
//            .emailNotification(EmailNotification.WEEKLY)
//            .build();
//
//        emailService.sendAddedNewPlacesReportEmail(sendReportEmailMessage);


//        HabitAssignNotificationMessage habitAssignNotificationMessage = HabitAssignNotificationMessage.builder()
//            .senderName("Maks")
//            .receiverName("Ilia")
//            .receiverEmail("rozhkoilia5533@gmail.com")
//            .habitName("Habit")
//            .habitAssignId(1L)
//            .language("en")
//            .build();
//
//        emailService.sendHabitAssignNotificationEmail(habitAssignNotificationMessage);
//
//        habitAssignNotificationMessage.setLanguage("ua");
//        emailService.sendHabitAssignNotificationEmail(habitAssignNotificationMessage);


//        emailService.sendCreateNewPasswordForEmployee(1L, "Ilia", "rozhkoilia5533@gmail.com", "123", "ua", true);
//        emailService.sendCreateNewPasswordForEmployee(1L, "Ilia", "rozhkoilia5533@gmail.com", "123", "en", false);


//        InterestingEcoNewsDto interestingEcoNewsDto = InterestingEcoNewsDto.builder()
//            .subscribers(List.of(
//                SubscriberDto.builder()
//                    .email("rozhkoilia5533@gmail.com")
//                    .name("Ilia")
//                    .unsubscribeToken(UUID.randomUUID())
//                    .language("en")
//                    .build()
//            ))
//            .ecoNewsList(List.of(
//                ShortEcoNewsDto.builder()
//                    .ecoNewsId(1L)
//                    .imagePath("https://csb10032000a548f571.blob.core.windows.net/allfiles/40b392f3-fa1f-4878-99f9-46a562210a8dIMG_3059.jpeg")
//                    .title("Title")
//                    .text("Text long text rewfd jotty rjndfu hhhas msmmmsmms masdjhwep idsafugqwrknl hfdjkwidu hwqebnmweosdgfbmqwerghou dhnwerui hnwekrfnqw kojvbcewuo ghroiewjfb oewhr foewn")
//                    .build()
//            ))
//            .build();
//
//        emailService.sendInterestingEcoNews(interestingEcoNewsDto);


//        ChangePlaceStatusDto changePlaceStatusDto = ChangePlaceStatusDto.builder()
//            .placeStatus(PlaceStatus.DECLINED)
//            .authorEmail("rozhkoilia5533@gmail.com")
//            .placeName("Dnipro")
//            .authorFirstName("Ilia")
//            .authorLanguage("ua")
//            .build();
//
//        emailService.sendChangePlaceStatusEmail(changePlaceStatusDto);
//
//        changePlaceStatusDto.setPlaceStatus(PlaceStatus.APPROVED);
//        changePlaceStatusDto.setAuthorLanguage("en");
//        emailService.sendChangePlaceStatusEmail(changePlaceStatusDto);
    }
}
