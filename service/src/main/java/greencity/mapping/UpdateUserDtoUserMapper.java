package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserDto;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserDtoUserMapper extends AbstractConverter<User, UpdateUserDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    @Override
    protected UpdateUserDto convert(User user) {
        Long userId = user.getId();

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
            .id(userId)
            .name(user.getName())
            .email(user.getEmail())
            .profilePicturePath(user.getProfilePicturePath())
            .userCredo(greenCityRemoteClient.findUserCredoByUserId(userId))
            .build();
        return updateUserDto;
    }
}
