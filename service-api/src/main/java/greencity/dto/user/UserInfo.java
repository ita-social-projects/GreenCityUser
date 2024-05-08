package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserInfo {
    private String sub;

    private String name;

    @JsonProperty("given_name")
    private String giveName;

    @JsonProperty("family_name")
    private String familyName;

    private String picture;

    private String email;

    @JsonProperty("email_verified")
    private String emailVerified;

    private String locale;
}
