package greencity.message;

import java.io.Serializable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class HabitAssignNotificationMessage implements Serializable {
    private String senderName;
    private String receiverName;
    private String receiverEmail;
    private String habitName;
    private String language;
    private Long habitAssignId;
}
