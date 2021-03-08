package greencity.service;

import greencity.entity.Achievement;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.enums.AchievementStatus;
import greencity.repository.UserAchievementRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Mock
    private UserAchievementRepo userAchievementRepo;

    @Test
    void findUserAchievement() {
        UserAchievement userAchievement = UserAchievement.builder()
            .id(1L)
            .user(new User())
            .achievement(new Achievement())
            .notified(false)
            .achievementStatus(AchievementStatus.INACTIVE)
            .build();
        when(userAchievementRepo.getUserAchievementByIdAndAchievementId(1L, 1L))
            .thenReturn(userAchievement);
        userAchievement.setAchievementStatus(AchievementStatus.ACTIVE);
        when(userAchievementRepo.save(userAchievement)).thenReturn(userAchievement);
        achievementService.findUserAchievement(1L, 1L);
    }
}