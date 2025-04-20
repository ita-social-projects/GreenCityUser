package greencity.service;

import greencity.client.GreenCityRemoteClient;
import greencity.constant.CacheConstants;
import greencity.dto.achievement.AchievementVO;
import java.util.List;
import greencity.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableCaching
public class AchievementServiceImpl implements AchievementService {
    private final GreenCityRemoteClient greenCityRemoteClient;

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Cacheable(value = CacheConstants.ALL_ACHIEVEMENTS_CACHE_NAME)
    @Override
    public List<AchievementVO> findAll() {
        return greenCityRemoteClient.findAllAchievements();
    }
}
