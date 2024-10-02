package greencity.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import greencity.annotations.ValidName;
import greencity.annotations.ValidSocialNetworkLinks;
import greencity.dto.CoordinatesDto;
import greencity.enums.EmailPreference;
import greencity.validator.BooleanValueDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoRequest {
    @ValidName
    @Schema(example = "John")
    private String name;

    @Size(max = 170)
    private String userCredo;

    @ValidSocialNetworkLinks
    private List<String> socialNetworks;

    @NotNull
    @JsonDeserialize(using = BooleanValueDeserializer.class)
    private Boolean showLocation;

    @NotNull
    @JsonDeserialize(using = BooleanValueDeserializer.class)
    private Boolean showEcoPlace;

    @NotNull
    @JsonDeserialize(using = BooleanValueDeserializer.class)
    private Boolean showShoppingList;

    private CoordinatesDto coordinates;

    private Set<UserNotificationPreferenceDto> emailPreferences;
}
