package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import greencity.ModelUtils;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    UserMapper mapper;

    @Test
    void convertTest() {
        User expectedResult = ModelUtils.getUser();
        VerifyEmail verifyEmail = VerifyEmail.builder()
            .id(1L)
            .token("token")
            .user(expectedResult)
            .build();
        expectedResult.setVerifyEmail(verifyEmail);
        Language language = ModelUtils.getLanguage();
        expectedResult.setLanguage(language);

        UserVO userToBeConverted = UserVO.builder()
            .id(expectedResult.getId())
            .name(expectedResult.getName())
            .email(expectedResult.getEmail())
            .role(expectedResult.getRole())
            .emailNotification(expectedResult.getEmailNotification())
            .userStatus(expectedResult.getUserStatus())
            .verifyEmail(expectedResult.getVerifyEmail() != null ? VerifyEmailVO.builder()
                .id(expectedResult.getVerifyEmail().getId())
                .user(UserVO.builder()
                    .id(expectedResult.getVerifyEmail().getUser().getId())
                    .name(expectedResult.getVerifyEmail().getUser().getName())
                    .build())
                .token(expectedResult.getVerifyEmail().getToken())
                .build() : null)
            .refreshTokenKey(expectedResult.getRefreshTokenKey())
            .ownSecurity(expectedResult.getOwnSecurity() != null ? OwnSecurityVO.builder()
                .id(expectedResult.getOwnSecurity().getId())
                .password(expectedResult.getOwnSecurity().getPassword())
                .user(UserVO.builder()
                    .id(expectedResult.getOwnSecurity().getUser().getId())
                    .email(expectedResult.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .dateOfRegistration(expectedResult.getDateOfRegistration())
            .showToDoList(expectedResult.getShowToDoList())
            .showEcoPlace(expectedResult.getShowEcoPlace())
            .showLocation(expectedResult.getShowLocation())
            .lastActivityTime(expectedResult.getLastActivityTime())
            .languageVO(ModelUtils.getLanguageVO())
            .firstName(expectedResult.getFirstName())
            .uuid(expectedResult.getUuid())
            .build();

        User actualResult = mapper.convert(userToBeConverted);

        assertEquals(expectedResult, actualResult);
    }
}
