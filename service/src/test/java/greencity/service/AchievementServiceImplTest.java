package greencity.service;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.entity.Achievement;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Mock
    private UserAchievementRepo userAchievementRepo;

    @Mock
    private AchievementRepo achievementRepo;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findUserAchievement() {
        UserAchievement userAchievement = UserAchievement.builder()
            .id(1L)
            .user(new User())
            .achievement(new Achievement())
            .notified(false)
            .build();
        when(userAchievementRepo.getUserAchievementByIdAndAchievementId(1L, 1L))
            .thenReturn(userAchievement);
        achievementService.findUserAchievement(1L, 1L);
        verify(userAchievementRepo).save(userAchievement);

    }

    @Test
    void findAllTest() {
        List<Achievement> achievementVOS = new ArrayList<>();
        achievementVOS.add(ModelUtils.getAchievement());
        when(achievementRepo.findAll()).thenReturn(achievementVOS);
        List<AchievementVO> allAchievements = achievementService.findAll();
        assertFalse(allAchievements.isEmpty());
    }

}