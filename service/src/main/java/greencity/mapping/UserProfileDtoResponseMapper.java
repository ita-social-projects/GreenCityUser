package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.dto.user.UserNotificationPreferenceDto;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserProfileDtoResponseMapper extends AbstractConverter<User, UserProfileDtoResponse> {
    private final GreenCityRemoteClient greenCityRemoteClient;
    private final ModelMapper modelMapper;

    @Lazy
    public UserProfileDtoResponseMapper(GreenCityRemoteClient greenCityRemoteClient, ModelMapper modelMapper) {
        this.greenCityRemoteClient = greenCityRemoteClient;
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserProfileDtoResponse convert(User user) {
        Long userId = user.getId();

        List<SocialNetworkResponseDTO> socialNetworks = user.getSocialNetworks().stream()
            .map(socialNetwork -> modelMapper.map(socialNetwork, SocialNetworkResponseDTO.class))
            .toList();

        Set<UserNotificationPreferenceDto> userNotificationPreferences = user.getNotificationPreferences().stream()
            .map(userNotificationPreference -> modelMapper.map(userNotificationPreference,
                UserNotificationPreferenceDto.class))
            .collect(Collectors.toSet());

        return UserProfileDtoResponse.builder()
            .profilePicturePath(user.getProfilePicturePath())
            .name(user.getName())
            .userCredo(greenCityRemoteClient.findUserCredoByUserId(userId))
            .socialNetworks(socialNetworks)
            .showLocation(user.getShowLocation())
            .showEcoPlace(user.getShowEcoPlace())
            .showToDoList(user.getShowToDoList())
            .rating(greenCityRemoteClient.findUserRatingByUserId(userId))
            .role(user.getRole())
            .userLocationDto(greenCityRemoteClient.findUserLocationByUserId(userId).orElse(null))
            .notificationPreferences(userNotificationPreferences)
            .build();
    }
}
