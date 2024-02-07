package greencity.dto;

import greencity.dto.position.PositionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePositionsDto {
    private String email;
    private List<PositionDto> positions;
}
