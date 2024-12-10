package greencity.message;

import greencity.enums.PlaceStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceStatusChangeDto {
    private String placeName;
    private PlaceStatus newStatus;
    private String userName;
    private String userEmail;
}
