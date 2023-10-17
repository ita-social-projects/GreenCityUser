package greencity.dto.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCityDto {
    @NotNull
    private Long id;
    private String cityEn;
    private String cityUa;
    private Double latitude;
    private double longitude;
}
