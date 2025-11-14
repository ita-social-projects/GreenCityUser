package greencity.properties;

import greencity.constant.ErrorMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Сlass for retrieving configuration values from the runtime environment. Used
 * to access dynamic properties. Provides a flexible alternative to @Value,
 * always getting the latest values without having to restart the application or
 * use /actuator/refresh.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getSenderEmailAddress();
        getGreenCityOfficeEmailAddress();
        getTelegramFeedbackEmailAddress();
        getSystemEmailAddress();
        log.info("All Email properties validated successfully.");
    }

    public String getSenderEmailAddress() {
        String senderEmail = environment.getProperty("contacts.sender.email-address");
        if (!StringUtils.hasText(senderEmail)) {
            log.error(ErrorMessage.SENDER_EMAIL_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.SENDER_EMAIL_ADDRESS_NOT_SET);
        }
        return senderEmail;
    }

    public String getGreenCityOfficeEmailAddress() {
        String officeEmailAddress = environment.getProperty("contacts.greenoffice.email-address");
        if (!StringUtils.hasText(officeEmailAddress)) {
            log.error(ErrorMessage.GREENCITY_OFFICE_EMAIL_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GREENCITY_OFFICE_EMAIL_ADDRESS_NOT_SET);
        }
        return officeEmailAddress;
    }

    public String getTelegramFeedbackEmailAddress() {
        String feedbackEmailAddress = environment.getProperty("contacts.tgbot.feedbacks-email-address");
        if (!StringUtils.hasText(feedbackEmailAddress)) {
            log.error(ErrorMessage.TELEGRAM_EMAIL_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.TELEGRAM_EMAIL_ADDRESS_NOT_SET);
        }
        return feedbackEmailAddress;
    }

    public String getSystemEmailAddress() {
        String systemEmailAddress = environment.getProperty("contacts.authorization.system-email-address");
        if (!StringUtils.hasText(systemEmailAddress)) {
            log.error(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET);
        }
        return systemEmailAddress;
    }
}
