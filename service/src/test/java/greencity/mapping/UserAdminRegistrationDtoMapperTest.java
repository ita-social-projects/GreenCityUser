package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.language.LanguageVO;
import greencity.entity.User;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminRegistrationDtoMapperTest {

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @InjectMocks
    UserAdminRegistrationDtoMapper mapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser();
        user.setUserStatus(UserStatus.BLOCKED);
        Long languageId = ModelUtils.getLanguageId();
        LanguageVO languageVO = ModelUtils.getLanguageVO();
        user.setLanguageId(languageId);
        user.setDateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47));

        when(greenCityRemoteClient.findLanguageById(languageId))
                        .thenReturn(languageVO);

        assertEquals(ModelUtils.getUserAdminRegistrationDto(), mapper.convert(user));
    }

}