package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.entity.Achievement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AchievementVOMapperTest {

    @InjectMocks
    private AchievementVOMapper achievementVOMapper;

    @Test
    void convert() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementVO expected = AchievementVO.builder()
            .id(achievement.getId())
            .title(achievement.getTitle())
            .name(achievement.getName())
            .nameEng(achievement.getNameEng())
            .achievementCategory(AchievementCategoryVO.builder()
                .id(achievement.getAchievementCategory().getId())
                .name(achievement.getAchievementCategory().getName())
                .build())
            .condition(achievement.getCondition())
            .build();
        assertEquals(expected, achievementVOMapper.convert(achievement));
    }
}