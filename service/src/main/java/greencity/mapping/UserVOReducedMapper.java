package greencity.mapping;

import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOReducedDto;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserVOReducedMapper extends AbstractConverter<User, UserVOReducedDto> {
    @Override
    protected UserVOReducedDto convert(User user) {
        Long userId = user.getId();
        Long languageId = user.getLanguageId();

        return UserVOReducedDto.builder()
                .id(userId)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .userCredo(user.getUserCredo())
                .emailNotification(user.getEmailNotification())
                .userStatus(user.getUserStatus())
                .verifyEmail(user.getVerifyEmail() != null ? VerifyEmailVO.builder()
                        .id(user.getVerifyEmail().getId())
                        .user(UserVO.builder()
                                .id(user.getVerifyEmail().getUser().getId())
                                .name(user.getVerifyEmail().getUser().getName())
                                .build())
                        .token(user.getVerifyEmail().getToken())
                        .build() : null)
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
                .showToDoList(user.getShowToDoList())
                .showEcoPlace(user.getShowEcoPlace())
                .showLocation(user.getShowLocation())
                .lastActivityTime(user.getLastActivityTime())
                .languageId(languageId)
                .firstName(user.getFirstName())
                .build();
    }
}
