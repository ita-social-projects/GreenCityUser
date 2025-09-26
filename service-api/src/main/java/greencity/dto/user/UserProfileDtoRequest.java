package greencity.dto.user;

import greencity.annotations.EnumValidation;
import greencity.annotations.ValidName;
import greencity.annotations.ValidSocialNetworkLinks;
import greencity.dto.CoordinatesDto;
import greencity.enums.ProfilePrivacyPolicy;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

    @ValidSocialNetworkLinks
    @ArraySchema(items = @Schema(example = "https://www.facebook.com/greencity"))
    private List<String> socialNetworks;

    @NotNull
    @EnumValidation(enumClass = ProfilePrivacyPolicy.class)
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showLocation;

    @NotNull
    @EnumValidation(enumClass = ProfilePrivacyPolicy.class)
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showEcoPlace;

    @NotNull
    @EnumValidation(enumClass = ProfilePrivacyPolicy.class)
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showToDoList;

    private CoordinatesDto coordinates;

    private Set<UserNotificationPreferenceDto> emailPreferences;
}
