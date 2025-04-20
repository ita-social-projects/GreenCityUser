package greencity.mapping;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserAdminRegistrationDto;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link User} into
 * {@link UserAdminRegistrationDto}.
 */
@Component
@RequiredArgsConstructor
public class UserAdminRegistrationDtoMapper extends AbstractConverter<User, UserAdminRegistrationDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    /**
     * Method convert {@link User} to {@link UserAdminRegistrationDto}.
     *
     * @return {@link UserAdminRegistrationDto}
     */
    @Override
    protected UserAdminRegistrationDto convert(User user) {
        Long languageId = user.getLanguageId();
        LanguageVO languageVO = greenCityRemoteClient.findLanguageById(languageId);

        return UserAdminRegistrationDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .dateOfRegistration(user.getDateOfRegistration())
            .userStatus(user.getUserStatus())
            .role(user.getRole())
            .languageCode(languageVO.getCode())
            .build();
    }
}
