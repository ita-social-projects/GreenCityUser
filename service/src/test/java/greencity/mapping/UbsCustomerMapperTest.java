package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.UbsCustomerDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UbsCustomerMapperTest {

    @InjectMocks
    private UbsCustomerMapper customerMapper;

    @Test
    void convert() {
        UbsCustomerDto expected = ModelUtils.getUbsCustomerDtoWithData();

        User userToBeConverted = User.builder()
            .email(expected.getEmail())
            .name(expected.getName())
            .build();
        assertEquals(expected, customerMapper.convert(userToBeConverted));
    }
}
