package greencity.mapping;

import greencity.dto.user.UserForListDto;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserForListDtoMapper extends AbstractConverter<User, UserForListDto> {
    @Override
    protected UserForListDto convert(User user) {
        return UserForListDto.builder()
            .id(user.getId())
            .name(user.getName())
            .dateOfRegistration(user.getDateOfRegistration())
            .email(user.getEmail())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .build();
    }
}
