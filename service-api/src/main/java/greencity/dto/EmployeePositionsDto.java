package greencity.dto;

import greencity.dto.position.PositionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePositionsDto {
    private String email;
    private List<PositionDto> positions;
}
