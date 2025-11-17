package greencity.properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.constant.ErrorMessage;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailPropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private EmailProperties emailProperties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(EmailProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getSenderEmailAddress_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("contacts.sender.email-address"))
            .thenReturn("sender@test.com");

        String result = emailProperties.getSenderEmailAddress();

        assertEquals("sender@test.com", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getSenderEmailAddress_shouldThrowException_whenMissing() {
        when(environment.getProperty("contacts.sender.email-address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> emailProperties.getSenderEmailAddress());

        assertEquals(ErrorMessage.SENDER_EMAIL_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.SENDER_EMAIL_ADDRESS_NOT_SET));
    }

    @Test
    void getGreenCityOfficeEmailAddress_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("contacts.greenoffice.email-address"))
            .thenReturn("office@test.com");

        String result = emailProperties.getGreenCityOfficeEmailAddress();

        assertEquals("office@test.com", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGreenCityOfficeEmailAddress_shouldThrowException_whenMissing() {
        when(environment.getProperty("contacts.greenoffice.email-address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> emailProperties.getGreenCityOfficeEmailAddress());

        assertEquals(ErrorMessage.GREENCITY_OFFICE_EMAIL_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GREENCITY_OFFICE_EMAIL_ADDRESS_NOT_SET));
    }

    @Test
    void getTelegramFeedbackEmailAddress_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("contacts.tgbot.feedbacks-email-address"))
            .thenReturn("tg@test.com");

        String result = emailProperties.getTelegramFeedbackEmailAddress();

        assertEquals("tg@test.com", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getTelegramFeedbackEmailAddress_shouldThrowException_whenMissing() {
        when(environment.getProperty("contacts.tgbot.feedbacks-email-address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> emailProperties.getTelegramFeedbackEmailAddress());

        assertEquals(ErrorMessage.TELEGRAM_EMAIL_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.TELEGRAM_EMAIL_ADDRESS_NOT_SET));
    }

    @Test
    void getSystemEmailAddress_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("contacts.authorization.system-email-address"))
            .thenReturn("system@test.com");

        String result = emailProperties.getSystemEmailAddress();

        assertEquals("system@test.com", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getSystemEmailAddress_shouldThrowException_whenMissing() {
        when(environment.getProperty("contacts.authorization.system-email-address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> emailProperties.getSystemEmailAddress());

        assertEquals(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenAllPropertiesValid() {
        when(environment.getProperty("contacts.sender.email-address")).thenReturn("s1@test.com");
        when(environment.getProperty("contacts.greenoffice.email-address")).thenReturn("s2@test.com");
        when(environment.getProperty("contacts.tgbot.feedbacks-email-address")).thenReturn("s3@test.com");
        when(environment.getProperty("contacts.authorization.system-email-address")).thenReturn("s4@test.com");

        emailProperties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All Email properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrowException_whenAnyPropertyMissing() {
        when(environment.getProperty("contacts.sender.email-address")).thenReturn("");
        when(environment.getProperty("contacts.greenoffice.email-address")).thenReturn("ok@test.com");
        when(environment.getProperty("contacts.tgbot.feedbacks-email-address")).thenReturn("ok@test.com");
        when(environment.getProperty("contacts.authorization.system-email-address")).thenReturn("ok@test.com");

        assertThrows(IllegalStateException.class,
            () -> emailProperties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.SENDER_EMAIL_ADDRESS_NOT_SET));
    }
}