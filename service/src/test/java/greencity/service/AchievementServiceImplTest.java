package greencity.service;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.entity.Achievement;
import greencity.repository.AchievementRepo;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Mock
    private AchievementRepo achievementRepo;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        List<Achievement> achievementVOS = new ArrayList<>();
        achievementVOS.add(ModelUtils.getAchievement());
        when(achievementRepo.findAll()).thenReturn(achievementVOS);
        List<AchievementVO> allAchievements = achievementService.findAll();
        assertFalse(allAchievements.isEmpty());
    }

}