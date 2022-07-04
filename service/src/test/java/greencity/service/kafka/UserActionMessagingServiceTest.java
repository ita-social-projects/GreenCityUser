package greencity.service.kafka;

import greencity.ModelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActionMessagingServiceTest {
    @InjectMocks
    private UserActionMessagingService service;

    @Value("user.actions")
    private String topic;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "enabled", true);
    }

    @Test
    void sendRegistrationEvent() {
        service.sendRegistrationEvent(ModelUtils.getUser());
        verify(kafkaTemplate).send(eq(topic), any());
    }

    @Test
    void sendFriendAddedEvent() {
        service.sendFriendAddedEvent(ModelUtils.getUser(), ModelUtils.getUser());
        verify(kafkaTemplate, times(2)).send(eq(topic), any());
    }
}