package greencity.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeactivateUserRequestDto {
    @NotBlank
    @NotNull
    private String reason;
}
