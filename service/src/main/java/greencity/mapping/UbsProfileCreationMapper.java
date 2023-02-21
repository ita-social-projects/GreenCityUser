package greencity.mapping;

import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;

public class UbsProfileCreationMapper extends AbstractConverter<User, UbsProfileCreationDto> {
    @Override
    protected UbsProfileCreationDto convert(User user) {
        return UbsProfileCreationDto.builder()
            .uuid(user.getUuid())
            .email(user.getEmail())
            .name(user.getName())
            .build();
    }
}
