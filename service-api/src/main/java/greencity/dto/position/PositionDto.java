package greencity.dto.position;

import greencity.dto.AuthorityDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class PositionDto {
    private Long id;
    private String name;
}
