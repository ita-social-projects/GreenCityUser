package greencity.service.kafka;

import greencity.dto.useraction.UserActionMessage;
import greencity.entity.User;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserActionMessagingService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.enable}")
    private boolean enabled;
    @Value("${kafka.topic.user.actions}")
    private String topic;

    private void sendMessage(UserActionMessage message) {
        if (enabled) {
            kafkaTemplate.send(topic, message);
        }
    }

    /**
     * Sends a {@link UserActionMessage} in the {@code greencity.user.actions} topic
     * with {@link UserActionType} {@code REGISTERED}.
     *
     * @param user {@link User} that has been registered.
     */
    public void sendRegistrationEvent(User user) {
        sendMessage(UserActionMessage.builder()
            .userEmail(user.getEmail())
            .actionType(UserActionType.REGISTERED)
            .contextType(ActionContextType.USER)
            .contextId(user.getId())
            .timestamp(ZonedDateTime.now()).build());
    }

    /**
     * Sends a {@link UserActionMessage} in the {@code greencity.user.actions} topic
     * with {@link UserActionType} {@code FRIEND_ADDED} for each user.
     *
     * @param user1 {@link User}
     * @param user2 {@link User}
     */
    public void sendFriendAddedEvent(User user1, User user2) {
        sendMessage(UserActionMessage.builder()
            .userEmail(user1.getEmail())
            .actionType(UserActionType.FRIEND_ADDED)
            .contextType(ActionContextType.USER)
            .contextId(user2.getId())
            .timestamp(ZonedDateTime.now()).build());
        sendMessage(UserActionMessage.builder()
            .userEmail(user2.getEmail())
            .actionType(UserActionType.FRIEND_ADDED)
            .contextType(ActionContextType.USER)
            .contextId(user1.getId())
            .timestamp(ZonedDateTime.now()).build());
    }
}
