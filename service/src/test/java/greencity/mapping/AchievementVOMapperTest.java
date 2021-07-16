package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementTranslationVO;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Achievement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AchievementVOMapperTest {

    @InjectMocks
    AchievementVOMapper mapper;

    @Test
    void convert() {
        List<AchievementTranslationVO> list = new ArrayList<>();
        Achievement achievement = ModelUtils.getAchievement();

        achievement.getTranslations().forEach(achievementTranslation -> list.add(AchievementTranslationVO.builder()
            .id(achievementTranslation.getId())
            .title(achievementTranslation.getTitle())
            .description(achievementTranslation.getDescription())
            .message(achievementTranslation.getMessage())
            .language(LanguageVO.builder()
                .id(achievementTranslation.getLanguage().getId())
                .code(achievementTranslation.getLanguage().getCode())
                .build())
            .build()));

        AchievementVO expected = AchievementVO.builder()
            .id(achievement.getId())
            .translations(list)
            .achievementCategory(AchievementCategoryVO.builder()
                .id(achievement.getAchievementCategory().getId())
                .name(achievement.getAchievementCategory().getName())
                .build())
            .condition(achievement.getCondition())
            .build();

        assertEquals(expected, mapper.convert(achievement));
    }
}