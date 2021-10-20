package greencity.dto.user;

import greencity.annotations.ValidSocialNetworkLinks;
import java.util.List;
import lombok.*;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoRequest {
    @Pattern(regexp = "[ЁёІіЇїҐґЄєА-Яа-яA-Za-z-'\\s.]{4,30}",
        message = "name must have no less than 4 and no more than 30 special symbols and contains only ЁёІіЇїҐґЄєА-Яа-яA-Za-z-' .")
    private String name;
    private String city;
    private String userCredo;
    @ValidSocialNetworkLinks
    private List<String> socialNetworks;
    private Boolean showLocation;
    private Boolean showEcoPlace;
    private Boolean showShoppingList;
}
