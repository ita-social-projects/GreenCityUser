package greencity.dto.newssubscriber;

import static greencity.constant.AppConstant.VALIDATION_EMAIL_REGEXP;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsSubscriberResponseDto implements Serializable {
    @NotBlank
    @Email(regexp = VALIDATION_EMAIL_REGEXP)
    private String email;
    @NotBlank
    private String unsubscribeToken;
}
