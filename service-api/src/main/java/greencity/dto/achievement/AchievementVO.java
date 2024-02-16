package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementVO {
    private Long id;
    private String title;
    private String name;
    private String nameEng;
    @NotEmpty
    private AchievementCategoryVO achievementCategory;
    @NotEmpty
    private Integer condition;
}
