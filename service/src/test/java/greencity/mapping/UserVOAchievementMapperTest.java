package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserVOAchievementMapperTest {

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @InjectMocks
    UserVOAchievementMapper userVOAchievementMapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        /*UserAchievement userAchievements = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievements));*/
        List<UserAchievementVO> userAchievements = Collections.singletonList(ModelUtils.getUserAchievement());

        when(greenCityRemoteClient.findAllUserAchievementsByUserId(userId))
                .thenReturn(userAchievements);

        UserVOAchievement expected = UserVOAchievement.builder()
            .id(user.getId())
            .name(user.getName())
            .userAchievements(userAchievements
                    .stream().map(userAchievement -> UserAchievementVO.builder()
                            .id(userAchievement.getId())
                            .user(UserVO.builder()
                                    .id(userAchievement.getUser().getId())
                                    .build())
                            .achievement(AchievementVO.builder()
                                    .id(userAchievement.getAchievement().getId())
                                    .build())
                            .build())
                    .collect(Collectors.toList()))
            .build();
        assertEquals(expected, userVOAchievementMapper.convert(user));
    }
}
