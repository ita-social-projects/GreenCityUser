package greencity.mapping;

import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getSocialNetworks;
import static greencity.ModelUtils.getUserVOAdvancedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserVOAdvancedDtoMapperTest {
    @InjectMocks
    private UserVOAdvancedDtoMapper mapper;

    @Test
    void convertTest() {
        UserVOAdvancedDto expectedResult = getUserVOAdvancedDto();
        User userToBeConverted = User.builder()
            .id(expectedResult.getId())
            .name(expectedResult.getName())
            .email(expectedResult.getEmail())
            .role(expectedResult.getRole())
            .userCredo(expectedResult.getUserCredo())
            .userStatus(expectedResult.getUserStatus())
            .dateOfRegistration(expectedResult.getDateOfRegistration())
            .profilePicturePath(expectedResult.getProfilePicturePath())
            .firstName(expectedResult.getFirstName())
            .languageId(1L)
            .socialNetworks(getSocialNetworks())
            .build();

        UserVOAdvancedDto actualResult = mapper.convert(userToBeConverted);

        assertEquals(expectedResult, actualResult);
    }
}
