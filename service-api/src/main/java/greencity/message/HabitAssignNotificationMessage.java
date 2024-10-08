package greencity.message;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HabitAssignNotificationMessage {
    @NotEmpty(message = "Sender name cannot be empty")
    private String senderName;

    @NotEmpty(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotEmpty(message = "Receiver email cannot be empty")
    @Email(message = "Receiver email should be valid")
    private String receiverEmail;

    @NotEmpty(message = "Habit name cannot be empty")
    private String habitName;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    @NotNull(message = "Habit assign ID cannot be null")
    private Long habitAssignId;
}
