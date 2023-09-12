package greencity.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendEventCreationNotificationTest {
    private SendEventCreationNotification notification;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        notification = new SendEventCreationNotification("test@gmail.com", "test-message-body");
    }

    @Test
    void toStringTest() {
        SendEventCreationNotification notificationBuild = SendEventCreationNotification.builder()
            .email("test@gmail.com")
            .messageBody("test-message-body")
            .build();
        assertEquals(notificationBuild.toString(), notification.toString());
    }

    @Test
    void noArgsConstructorTest() {
        notification = new SendEventCreationNotification();
        SendEventCreationNotification notificationBuild = SendEventCreationNotification.builder().build();

        assertEquals(notificationBuild.getEmail(), notification.getEmail());
        assertEquals(notificationBuild.getMessageBody(), notification.getMessageBody());
    }
}