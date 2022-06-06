package greencity.service;

import greencity.dto.useraction.UserActionMessage;
import greencity.entity.User;
import greencity.enums.UserActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public static final String USER_ACTIONS_TOPIC = "greencity.user.actions";

    /**
     * Sends a {@link UserActionMessage} in the {@code greencity.user.actions} topic
     * with {@link UserActionType} {@code REGISTERED}.
     *
     * @param user {@link User} that has been registered.
     */
    public void sendRegistrationEvent(User user) {
        kafkaTemplate.send(USER_ACTIONS_TOPIC,
            UserActionMessage.builder()
                .userEmail(user.getEmail())
                .actionType(UserActionType.REGISTERED)
                .actionId(user.getId())
                .timestamp(ZonedDateTime.now().toString()).build());
    }

    /**
     * Sends a {@link UserActionMessage} in the {@code greencity.user.actions} topic
     * with {@link UserActionType} {@code FRIEND_ADDED} for each user.
     *
     * @param user1 {@link User}
     * @param user2 {@link User}
     */
    public void sendFriendAddedEvent(User user1, User user2) {
        kafkaTemplate.send(USER_ACTIONS_TOPIC,
            UserActionMessage.builder()
                .userEmail(user1.getEmail())
                .actionType(UserActionType.FRIEND_ADDED)
                .actionId(user2.getId())
                .timestamp(ZonedDateTime.now().toString()).build());
        kafkaTemplate.send(USER_ACTIONS_TOPIC,
            UserActionMessage.builder()
                .userEmail(user2.getEmail())
                .actionType(UserActionType.FRIEND_ADDED)
                .actionId(user1.getId())
                .timestamp(ZonedDateTime.now().toString()).build());
    }
}
