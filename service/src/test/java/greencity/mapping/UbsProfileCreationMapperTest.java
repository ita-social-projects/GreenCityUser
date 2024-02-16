package greencity.mapping;

import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getUbsProfileCreationDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UbsProfileCreationMapperTest {
    @InjectMocks
    private UbsProfileCreationMapper mapper;

    @Test
    void convert() {
        UbsProfileCreationDto expected = getUbsProfileCreationDto();
        User user = User.builder()
            .uuid(expected.getUuid())
            .email(expected.getEmail())
            .name(expected.getName())
            .build();
        assertEquals(expected, mapper.convert(user));
    }
}
