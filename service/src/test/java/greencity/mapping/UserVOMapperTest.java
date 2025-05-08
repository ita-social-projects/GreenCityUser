package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.OwnSecurity;

import static greencity.ModelUtils.getSocialNetworks;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserVOMapperTest {
    @InjectMocks
    UserVOMapper mapper;

    @Test
    void convertTest() {
        UserVO expectedResult = ModelUtils.getUserVOWithData();

        User userToBeConverted = User.builder()
            .id(expectedResult.getId())
            .name(expectedResult.getName())
            .email(expectedResult.getEmail())
            .role(expectedResult.getRole())
            .emailNotification(expectedResult.getEmailNotification())
            .userStatus(expectedResult.getUserStatus())
            // .rating(expectedResult.getRating())
            .verifyEmail(expectedResult.getVerifyEmail() != null ? VerifyEmail.builder()
                .id(expectedResult.getVerifyEmail().getId())
                .user(User.builder()
                    .id(expectedResult.getVerifyEmail().getUser().getId())
                    .name(expectedResult.getVerifyEmail().getUser().getName())
                    .build())
                .token(expectedResult.getVerifyEmail().getToken())
                .build() : null)
            /*
             * .userFriends(expectedResult.getUserFriends() != null ?
             * expectedResult.getUserFriends() .stream().map(user1 -> User.builder()
             * .id(user1.getId()) .name(user1.getName()) .build())
             * .collect(Collectors.toList()) : null)
             */
            .refreshTokenKey(expectedResult.getRefreshTokenKey())
            .dateOfRegistration(expectedResult.getDateOfRegistration())
            .profilePicturePath(expectedResult.getProfilePicturePath())
            /*
             * .userLocation( UserLocation.builder()
             * .id(expectedResult.getUserLocationDto().getId())
             * .cityEn(expectedResult.getUserLocationDto().getCityEn())
             * .cityUk(expectedResult.getUserLocationDto().getCityUk())
             * .regionEn(expectedResult.getUserLocationDto().getRegionEn())
             * .regionUk(expectedResult.getUserLocationDto().getRegionUk())
             * .countryEn(expectedResult.getUserLocationDto().getCountryEn())
             * .countryUk(expectedResult.getUserLocationDto().getCountryUk())
             * .latitude(expectedResult.getUserLocationDto().getLatitude())
             * .longitude(expectedResult.getUserLocationDto().getLongitude()) .users(null)
             * .build())
             */
            .showToDoList(expectedResult.getShowToDoList())
            .showEcoPlace(expectedResult.getShowEcoPlace())
            .showLocation(expectedResult.getShowLocation())
            .ownSecurity(expectedResult.getOwnSecurity() != null ? OwnSecurity.builder()
                .id(expectedResult.getOwnSecurity().getId())
                .password(expectedResult.getOwnSecurity().getPassword())
                .user(User.builder()
                    .id(expectedResult.getOwnSecurity().getUser().getId())
                    .email(expectedResult.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .lastActivityTime(expectedResult.getLastActivityTime())
            .firstName(expectedResult.getFirstName())
            .language(ModelUtils.getLanguage())
            .socialNetworks(getSocialNetworks())
            .build();

        UserVO actualResult = mapper.convert(userToBeConverted);

        assertEquals(expectedResult, actualResult);
    }
}
