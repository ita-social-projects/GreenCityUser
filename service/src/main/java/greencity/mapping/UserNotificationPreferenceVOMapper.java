package greencity.mapping;

import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationPreferenceVOMapper extends AbstractConverter<UserNotificationPreference, UserNotificationPreferenceVO> {

    private final ModelMapper modelMapper;

    @Lazy
    public UserNotificationPreferenceVOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserNotificationPreferenceVO convert(UserNotificationPreference userNotificationPreference) {
        User user = userNotificationPreference.getUser();
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserNotificationPreferenceVO.builder()
                .id(userNotificationPreference.getId())
                .user(userVO)
                .emailPreference(userNotificationPreference.getEmailPreference())
                .periodicity(userNotificationPreference.getPeriodicity())
                .build();
    }
}
