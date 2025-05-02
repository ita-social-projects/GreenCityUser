package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserLocationDto {
    private Long id;
    private String cityEn;
    private String cityUk;
    private String regionEn;
    private String regionUk;
    private String countryEn;
    private String countryUk;
    private Double latitude;
    private Double longitude;
}
