package greencity.service.kafka;

import greencity.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class UserActionMessagingServiceTest {
    @InjectMocks
    private UserActionMessagingService service;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendRegistrationEvent() {
        service.sendRegistrationEvent(ModelUtils.getUser());
    }

    @Test
    void sendFriendAddedEvent() {
        service.sendFriendAddedEvent(ModelUtils.getUser(), ModelUtils.getUser());
    }
}