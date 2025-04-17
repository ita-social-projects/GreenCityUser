package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import greencity.entity.UserLocation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserVOMapper extends AbstractConverter<User, UserVO> {

    private final GreenCityRemoteClient greenCityRemoteClient;

    @Override
    protected UserVO convert(User user) {
        Long userId = user.getId();
        Long languageId = user.getLanguageId();
        List<UserAchievementVO> userAchievements = greenCityRemoteClient.findAllUserAchievementsByUserId(userId);
        List<UserActionVO> userActions = greenCityRemoteClient.findAllUserActionsByUserId(userId);
        LanguageVO languageVO = greenCityRemoteClient.findLanguageById(languageId);

        return UserVO.builder()
            .id(userId)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .userCredo(user.getUserCredo())
            .emailNotification(user.getEmailNotification())
            .userStatus(user.getUserStatus())
            .rating(user.getRating())
            .verifyEmail(user.getVerifyEmail() != null ? VerifyEmailVO.builder()
                .id(user.getVerifyEmail().getId())
                .user(UserVO.builder()
                    .id(user.getVerifyEmail().getUser().getId())
                    .name(user.getVerifyEmail().getUser().getName())
                    .build())
                .token(user.getVerifyEmail().getToken())
                .build() : null)
            .userFriends(user.getUserFriends() != null ? user.getUserFriends()
                .stream().map(user1 -> UserVO.builder()
                    .id(user1.getId())
                    .name(user1.getName())
                    .build())
                .toList() : null)
            .refreshTokenKey(user.getRefreshTokenKey())
            .ownSecurity(user.getOwnSecurity() != null ? OwnSecurityVO.builder()
                .id(user.getOwnSecurity().getId())
                .password(user.getOwnSecurity().getPassword())
                .user(UserVO.builder()
                    .id(user.getOwnSecurity().getUser().getId())
                    .email(user.getOwnSecurity().getUser().getEmail())
                    .role(user.getOwnSecurity().getUser().getRole())
                    .build())
                .build() : null)
            .dateOfRegistration(user.getDateOfRegistration())
            .userLocationDto(convertUserLocationToDto(user.getUserLocation()))
            .profilePicturePath(user.getProfilePicturePath())
            .showToDoList(user.getShowToDoList())
            .showEcoPlace(user.getShowEcoPlace())
            .showLocation(user.getShowLocation())
            .lastActivityTime(user.getLastActivityTime())
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
            .userActions(userActions
                .stream().map(userAction -> UserActionVO.builder()
                    .id(userAction.getId())
                    .achievementCategory(AchievementCategoryVO.builder()
                        .id(userAction.getAchievementCategory().getId())
                        .build())
                    .count(userAction.getCount())
                    .user(UserVO.builder()
                        .id(userAction.getUser().getId())
                        .build())
                    .build())
                .toList())
            .languageVO(LanguageVO.builder()
                .id(languageVO.getId())
                .code(languageVO.getCode())
                .build())
            .build();
    }

    private UserLocationDto convertUserLocationToDto(UserLocation userLocation) {
        return Optional.ofNullable(userLocation)
            .map(ul -> UserLocationDto.builder()
                .id(ul.getId())
                .cityEn(ul.getCityEn())
                .cityUk(ul.getCityUk())
                .regionEn(ul.getRegionEn())
                .regionUk(ul.getRegionUk())
                .countryEn(ul.getCountryEn())
                .countryUk(ul.getCountryUk())
                .latitude(ul.getLatitude())
                .longitude(ul.getLongitude())
                .build())
            .orElse(null);
    }
}
