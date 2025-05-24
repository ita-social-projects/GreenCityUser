package greencity.mapping;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserLocationDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserForListDtoMapperTest {

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @InjectMocks
    UserForListDtoMapper userForListDtoMapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String userCredo = TestConst.CREDO;
        String profilePicturePath = "profilePicturePath";
        var greenCityUserProfile =
            new GreenCityUserProfileDtoResponse(userId, profilePicturePath, userCredo, 0., new UserLocationDto());

        UserForListDto expectedResult = UserForListDto.builder()
            .id(userId)
            .name(user.getName())
            .dateOfRegistration(user.getDateOfRegistration())
            .email(user.getEmail())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .userCredo(userCredo)
            .build();

        when(greenCityRemoteClient.findGreenCityUserProfileByUserId(userId))
            .thenReturn(greenCityUserProfile);

        UserForListDto actualResult = userForListDtoMapper.convert(user);

        assertEquals(expectedResult, actualResult);
    }
}
