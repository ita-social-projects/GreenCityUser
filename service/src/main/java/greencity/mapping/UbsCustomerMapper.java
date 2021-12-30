package greencity.mapping;

import greencity.dto.UbsCustomerDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UbsCustomerMapper extends AbstractConverter<User, UbsCustomerDto> {
    @Override
    protected UbsCustomerDto convert(User user) {
        return UbsCustomerDto.builder()
            .email(user.getEmail())
            .phoneNumber(user.getName())
            .name(user.getName())
            .build();
    }
}
