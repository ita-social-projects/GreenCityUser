package greencity.service;

import greencity.constant.CacheConstants;
import greencity.dto.achievement.AchievementVO;
import greencity.entity.UserAchievement;
import greencity.repository.AchievementRepo;
import greencity.repository.UserAchievementRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@EnableCaching
public class AchievementServiceImpl implements AchievementService {
    private final AchievementRepo achievementRepo;
    private final ModelMapper modelMapper;
    private final UserAchievementRepo userAchievementRepo;

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Cacheable(value = CacheConstants.ALL_ACHIEVEMENTS_CACHE_NAME)
    @Override
    public List<AchievementVO> findAll() {
        return achievementRepo.findAll()
            .stream()
            .map(achieve -> modelMapper.map(achieve, AchievementVO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findUserAchievement(Long userId, Long achievementId) {
        UserAchievement userAchievement = userAchievementRepo
            .getUserAchievementByIdAndAchievementId(userId, achievementId);
        userAchievementRepo.save(userAchievement);
    }
}
