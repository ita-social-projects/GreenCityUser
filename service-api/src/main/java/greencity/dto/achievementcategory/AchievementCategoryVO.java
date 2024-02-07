package greencity.dto.achievementcategory;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.useraction.UserActionVO;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AchievementCategoryVO {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    private List<AchievementVO> achievementList;

    private List<UserActionVO> userActions;
}
