package greencity.dto.authorities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityDto {
    private String name;
    private String descriptionEn;
    private String descriptionUk;
}
