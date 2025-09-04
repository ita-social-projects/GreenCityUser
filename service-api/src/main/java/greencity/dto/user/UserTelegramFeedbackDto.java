package greencity.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserTelegramFeedbackDto {
    @NotBlank
    @Size(max = 64)
    private String chatId;

    @Size(max = 255)
    private String name;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 2000)
    private String comment;

    @Size(max = 255)
    @Pattern(regexp = "^[^\r\n]*$", message = "Subject must not contain CR or LF characters")
    private String subject;
}
