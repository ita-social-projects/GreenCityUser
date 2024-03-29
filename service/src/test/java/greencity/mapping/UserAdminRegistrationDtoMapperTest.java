package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAdminRegistrationDtoMapperTest {

    @InjectMocks
    UserAdminRegistrationDtoMapper mapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser();
        user.setUserStatus(UserStatus.BLOCKED);
        user.setLanguage(Language.builder().id(2L).code("en").build());
        user.setDateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47));

        assertEquals(ModelUtils.getUserAdminRegistrationDto(), mapper.convert(user));
    }

}