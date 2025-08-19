package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.OwnSecurity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            .verifyEmail(expectedResult.getVerifyEmail() != null ? VerifyEmail.builder()
                .id(expectedResult.getVerifyEmail().getId())
                .user(User.builder()
                    .id(expectedResult.getVerifyEmail().getUser().getId())
                    .name(expectedResult.getVerifyEmail().getUser().getName())
                    .build())
                .token(expectedResult.getVerifyEmail().getToken())
                .build() : null)
            .refreshTokenKey(expectedResult.getRefreshTokenKey())
            .ownSecurity(expectedResult.getOwnSecurity() != null ? OwnSecurity.builder()
                .id(expectedResult.getOwnSecurity().getId())
                .password(expectedResult.getOwnSecurity().getPassword())
                .user(User.builder()
                    .id(expectedResult.getOwnSecurity().getUser().getId())
                    .email(expectedResult.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .dateOfRegistration(expectedResult.getDateOfRegistration())
            .showToDoList(expectedResult.getShowToDoList())
            .showEcoPlace(expectedResult.getShowEcoPlace())
            .showLocation(expectedResult.getShowLocation())
            .lastActivityTime(expectedResult.getLastActivityTime())
            .language(ModelUtils.getLanguage())
            .firstName(expectedResult.getFirstName())
            .build();

        UserVO actualResult = mapper.convert(userToBeConverted);

        assertEquals(expectedResult, actualResult);
    }
}
