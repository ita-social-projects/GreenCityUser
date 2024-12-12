package greencity.message;

import greencity.enums.PlaceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceStatusChangeDto {
    @NotNull
    private String placeName;

    @NotNull
    private PlaceStatus newStatus;

    @NotNull
    private String userName;

    @NotNull
    private String email;
}
