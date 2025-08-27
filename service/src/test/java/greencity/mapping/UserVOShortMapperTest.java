package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVOShort;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getSocialNetworks;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserVOShortMapperTest {
    @InjectMocks
    private UserVOShortMapper mapper;

    @Test
    void convertTest() {
        UserVOShort expected = ModelUtils.getUserVOShortDto();

        User userToBeConverted = User.builder()
            .id(expected.getId())
            .name(expected.getName())
            .email(expected.getEmail())
            .role(expected.getRole())
            .userStatus(expected.getUserStatus())
            .language(ModelUtils.getLanguage())
            .socialNetworks(getSocialNetworks())
            .build();

        UserVOShort actual = mapper.convert(userToBeConverted);
        assertEquals(expected, actual);
    }
}
