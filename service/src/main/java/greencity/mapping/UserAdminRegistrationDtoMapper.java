package greencity.mapping;

import greencity.dto.user.UserAdminRegistrationDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link User} into
 * {@link UserAdminRegistrationDto}.
 */
@Component
public class UserAdminRegistrationDtoMapper extends AbstractConverter<User, UserAdminRegistrationDto> {
    /**
     * Method convert {@link User} to {@link UserAdminRegistrationDto}.
     *
     * @return {@link UserAdminRegistrationDto}
     */
    @Override
    protected UserAdminRegistrationDto convert(User user) {
        return UserAdminRegistrationDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .dateOfRegistration(user.getDateOfRegistration())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .languageCode(user.getLanguage().getCode())
            .build();
    }
}
