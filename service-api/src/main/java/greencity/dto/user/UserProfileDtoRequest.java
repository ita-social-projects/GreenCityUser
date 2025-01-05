package greencity.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import greencity.annotations.EnumValidation;
import greencity.annotations.ValidName;
import greencity.annotations.ValidSocialNetworkLinks;
import greencity.dto.CoordinatesDto;
import greencity.enums.EcoPlacePrivacyPolicy;
import greencity.enums.LocationPrivacyPolicy;
import greencity.enums.ToDoListPrivacyPolicy;
import greencity.validator.BooleanValueDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Enumerated;
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
    @EnumValidation(enumClass = LocationPrivacyPolicy.class)
    private LocationPrivacyPolicy showLocation;

    @NotNull
    @EnumValidation(enumClass = EcoPlacePrivacyPolicy.class)
    private EcoPlacePrivacyPolicy showEcoPlace;

    @NotNull
    @EnumValidation(enumClass = ToDoListPrivacyPolicy.class)
    private ToDoListPrivacyPolicy showToDoList;

    private CoordinatesDto coordinates;

    private Set<UserNotificationPreferenceDto> emailPreferences;
}
