package greencity.message;

import greencity.annotations.ValidPlaceStatus;
import greencity.enums.PlaceStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePlaceStatusDto {
    @NotEmpty(message = "Author's first name cannot be empty")
    private String authorFirstName;

    @NotEmpty(message = "Author's language cannot be empty")
    private String authorLanguage;

    @NotEmpty(message = "Place name cannot be empty")
    private String placeName;

    @ValidPlaceStatus
    private PlaceStatus placeStatus;

    @NotEmpty(message = "Author's email cannot be empty")
    private String authorEmail;
}
