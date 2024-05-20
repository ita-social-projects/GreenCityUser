package greencity.message;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

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
