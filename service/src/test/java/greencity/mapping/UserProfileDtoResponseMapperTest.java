package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserNotificationPreferenceDto;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.entity.SocialNetwork;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileDtoResponseMapperTest {
    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserProfileDtoResponseMapper userProfileDtoResponseMapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        user.setSocialNetworks(List.of(new SocialNetwork()));
        user.setNotificationPreferences(Set.of(new UserNotificationPreference()));
        Long userId = user.getId();
        Double userRating = 4.;
        String profilePicturePath = "http://testpicture.com.ua";
        UserLocationDto userLocationDto = new UserLocationDto();
        SocialNetworkResponseDTO socialNetworkResponseDTO = new SocialNetworkResponseDTO();
        List<SocialNetworkResponseDTO> expectedSocialNetworks = List.of(socialNetworkResponseDTO);
        UserNotificationPreferenceDto userNotificationPreferenceDto = new UserNotificationPreferenceDto();
        Set<UserNotificationPreferenceDto> expectedNotificationPreferences = Set.of(userNotificationPreferenceDto);
        var greenCityUserProfile =
            new GreenCityUserProfileDtoResponse(userId, profilePicturePath, userRating, userLocationDto);

        when(greenCityRemoteClient.findGreenCityUserProfileByUserId(userId)).thenReturn(greenCityUserProfile);

        UserProfileDtoResponse expectedResult = UserProfileDtoResponse.builder()
            .profilePicturePath(profilePicturePath)
            .name(user.getName())
            .socialNetworks(expectedSocialNetworks)
            .showLocation(user.getShowLocation())
            .showEcoPlace(user.getShowEcoPlace())
            .showToDoList(user.getShowToDoList())
            .rating(userRating)
            .role(user.getRole())
            .userLocationDto(userLocationDto)
            .notificationPreferences(expectedNotificationPreferences)
            .build();

        when(modelMapper.map(any(SocialNetwork.class), eq(SocialNetworkResponseDTO.class)))
            .thenReturn(socialNetworkResponseDTO);
        when(modelMapper.map(any(UserNotificationPreference.class), eq(UserNotificationPreferenceDto.class)))
            .thenReturn(userNotificationPreferenceDto);

        UserProfileDtoResponse actualResult = userProfileDtoResponseMapper.convert(user);

        assertEquals(expectedResult, actualResult);
    }
}
