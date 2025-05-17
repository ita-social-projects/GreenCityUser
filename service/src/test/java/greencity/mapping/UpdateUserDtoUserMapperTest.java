package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UpdateUserDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UpdateUserDtoUserMapperTest {

    @InjectMocks
    private UpdateUserDtoUserMapper mapper;

    @Test
    void convertTest() {
        User userToConvert = ModelUtils.getUser();
        UpdateUserDto expected = ModelUtils.getUpdateUserDto();

        UpdateUserDto result = mapper.convert(userToConvert);

        assertEquals(expected, result);
    }
}
