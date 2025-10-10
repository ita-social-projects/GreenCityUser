package greencity.mapping;

import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractConverter<UserVO, User> {
    @Override
    protected User convert(UserVO user) {
        Long userId = user.getId();

        return User.builder()
            .id(userId)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .emailNotification(user.getEmailNotification())
            .userStatus(user.getUserStatus())
            .verifyEmail(user.getVerifyEmail() != null ? VerifyEmail.builder()
                .id(user.getVerifyEmail().getId())
                .user(User.builder()
                    .id(user.getVerifyEmail().getUser().getId())
                    .name(user.getVerifyEmail().getUser().getName())
                    .build())
                .token(user.getVerifyEmail().getToken())
                .build() : null)
            .refreshTokenKey(user.getRefreshTokenKey())
            .ownSecurity(user.getOwnSecurity() != null ? OwnSecurity.builder()
                .id(user.getOwnSecurity().getId())
                .password(user.getOwnSecurity().getPassword())
                .user(User.builder()
                    .id(user.getOwnSecurity().getUser().getId())
                    .email(user.getOwnSecurity().getUser().getEmail())
                    .role(user.getOwnSecurity().getUser().getRole())
                    .build())
                .build() : null)
            .dateOfRegistration(user.getDateOfRegistration())
            .showToDoList(user.getShowToDoList())
            .showEcoPlace(user.getShowEcoPlace())
            .showLocation(user.getShowLocation())
            .lastActivityTime(user.getLastActivityTime())
            .language(Language.builder()
                .id(user.getLanguageVO().getId())
                .code(user.getLanguageVO().getCode())
                .build())
            .firstName(user.getFirstName())
            .uuid(user.getUuid())
            .build();
    }
}
