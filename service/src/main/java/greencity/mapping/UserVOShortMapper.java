package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserVOShort;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserVOShortMapper extends AbstractConverter<User, UserVOShort> {
    @Override
    protected UserVOShort convert(User user) {
        return UserVOShort.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .userStatus(user.getUserStatus())
            .languageVO(LanguageVO.builder()
                .id(user.getLanguage().getId())
                .code(user.getLanguage().getCode())
                .build())
            .build();
    }
}
