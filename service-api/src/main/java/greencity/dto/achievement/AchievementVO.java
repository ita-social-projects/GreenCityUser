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
    private String nameUk;
    private String nameEn;
    @NotEmpty
    private AchievementCategoryVO achievementCategory;
    @NotEmpty
    private Integer condition;
}
