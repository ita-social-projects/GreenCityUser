package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserVOAchievementMapper extends AbstractConverter<User, UserVOAchievement> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    @Override
    protected UserVOAchievement convert(User user) {
        Long userId = user.getId();
        List<UserAchievementVO> userAchievements = greenCityRemoteClient.findAllUserAchievementsByUserId(userId);

        return UserVOAchievement.builder()
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
                .toList())
            .build();
    }
}
