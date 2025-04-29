package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UpdateUserDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UpdateUserDtoUserMapper extends AbstractConverter<User, UpdateUserDto> {
    @Override
    protected UpdateUserDto convert(User user) {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .userCredo(user.getUserCredo())
                .profilePicturePath(user.getProfilePicturePath())
                .build();
        String languageCode;
        if (Objects.equals(user.getLanguageId(), 1L)) {
            languageCode = AppConstant.UA_LANGUAGE_CODE;
        } else {
            languageCode = AppConstant.DEFAULT_LANGUAGE_CODE;
        }
        updateUserDto.setLanguage(LanguageVO.builder()
                .code(languageCode)
                .id(user.getLanguageId())
                .build());
        return updateUserDto;
    }
}
