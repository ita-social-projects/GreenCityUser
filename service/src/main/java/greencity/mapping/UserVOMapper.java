package greencity.mapping;

import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    @Override
    protected UserVO convert(User user) {
        Long userId = user.getId();
        Long languageId = user.getLanguageId();

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
            .profilePicturePath(user.getProfilePicturePath())
            .showToDoList(user.getShowToDoList())
            .showEcoPlace(user.getShowEcoPlace())
            .showLocation(user.getShowLocation())
            .lastActivityTime(user.getLastActivityTime())
            .languageId(languageId)
            .build();
    }
}
