package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVOReducedDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getSocialNetworks;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserVOReducedMapperTest {
    @InjectMocks
    private UserVOReducedMapper mapper;

    @Test
    void convertTest() {
        UserVOReducedDto expectedResult = ModelUtils.getUserVOReducedDto();

        User userToBeConverted = User.builder()
                .id(expectedResult.getId())
                .name(expectedResult.getName())
                .email(expectedResult.getEmail())
                .role(expectedResult.getRole())
                .userStatus(expectedResult.getUserStatus())
                .dateOfRegistration(expectedResult.getDateOfRegistration())
                .firstName(expectedResult.getFirstName())
                .language(ModelUtils.getLanguage())
                .socialNetworks(getSocialNetworks())
                .lastActivityTime(expectedResult.getLastActivityTime())
                .build();

        UserVOReducedDto actualResult = mapper.convert(userToBeConverted);
        assertEquals(expectedResult, actualResult);
    }
}
