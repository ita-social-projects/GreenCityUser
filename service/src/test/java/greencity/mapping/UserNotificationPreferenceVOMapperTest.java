package greencity.mapping;

import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationPreferenceVOMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserNotificationPreferenceVOMapper userNotificationPreferenceVOMapper;

    @Test
    void convertTest() {
        UserNotificationPreference userNotificationPreference = mock(UserNotificationPreference.class);
        User user = mock(User.class);
        UserVO userVO = mock(UserVO.class);
        Long userNotificationPreferenceId = 1L;
        EmailPreference emailPreference = EmailPreference.COMMENTS;
        EmailPreferencePeriodicity emailPreferencePeriodicity = EmailPreferencePeriodicity.DAILY;
        UserNotificationPreferenceVO expectedResult = UserNotificationPreferenceVO.builder()
                .id(userNotificationPreferenceId)
                .user(userVO)
                .emailPreference(emailPreference)
                .periodicity(emailPreferencePeriodicity)
                .build();

        when(userNotificationPreference.getUser())
                .thenReturn(user);
        when(modelMapper.map(user, UserVO.class))
                .thenReturn(userVO);
        when(userNotificationPreference.getId())
                .thenReturn(userNotificationPreferenceId);
        when(userNotificationPreference.getEmailPreference())
                .thenReturn(emailPreference);
        when(userNotificationPreference.getPeriodicity())
                .thenReturn(emailPreferencePeriodicity);

        UserNotificationPreferenceVO actualResult = userNotificationPreferenceVOMapper.convert(userNotificationPreference);

        assertEquals(expectedResult, actualResult);
    }

}
