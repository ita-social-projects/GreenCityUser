package greencity.dto.authorities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityCategoryDto {
    private Long id;
    private String nameEn;
    private String nameUk;
    private List<AuthorityDto> authorities;
}
