package greencity.dto.position;

import lombok.*;

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
