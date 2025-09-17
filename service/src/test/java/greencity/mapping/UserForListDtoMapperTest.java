package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserForListDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserForListDtoMapperTest {
    UserForListDtoMapper userForListDtoMapper = new UserForListDtoMapper();

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();

        UserForListDto expectedResult = UserForListDto.builder()
            .id(user.getId())
            .name(user.getName())
            .dateOfRegistration(user.getDateOfRegistration())
            .email(user.getEmail())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .build();

        UserForListDto actualResult = userForListDtoMapper.convert(user);

        assertEquals(expectedResult, actualResult);
    }
}
