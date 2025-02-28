package greencity.dto.violation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserViolationMailDto {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String language;
    private String violationDescription;
}
