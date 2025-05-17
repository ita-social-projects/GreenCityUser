package greencity.mapping;

import greencity.dto.user.UpdateUserDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserDtoUserMapper extends AbstractConverter<User, UpdateUserDto> {
    @Override
    protected UpdateUserDto convert(User user) {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .build();
        return updateUserDto;
    }
}
