package greencity.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import greencity.annotations.ValidSocialNetworkLinks;
import greencity.dto.CoordinatesDto;
import greencity.validator.BooleanValueDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoRequest {
    @Size(min = 1, max = 30, message = "name must have no less than 1 and no more than 30 symbols")
    @Pattern(regexp = "^(?!\\.)(?!.*\\.$)(?!.*?\\.\\.)(?!.*?\\-\\-)(?!.*?\\'\\')[-'ʼ ґҐіІєЄїЇА-Яа-я+\\w.]{1,30}$",
        message = "name must contain only \"ЁёІіЇїҐґЄєА-Яа-яA-Za-z-'0-9 .\", dot can only be in the center of the name")
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
}
