package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UserForListDto;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserForListDtoMapper extends AbstractConverter<User, UserForListDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    @Override
    protected UserForListDto convert(User user) {
        Long userId = user.getId();
        var greenCityUserProfile = greenCityRemoteClient.findGreenCityUserProfileByUserId(userId);

        return UserForListDto.builder()
            .id(userId)
            .name(user.getName())
            .dateOfRegistration(user.getDateOfRegistration())
            .email(user.getEmail())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .userCredo(greenCityUserProfile.userCredo())
            .build();
    }
}
