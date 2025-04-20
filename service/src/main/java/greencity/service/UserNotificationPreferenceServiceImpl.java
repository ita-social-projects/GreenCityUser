package greencity.service;

import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.repository.UserNotificationPreferenceRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationPreferenceServiceImpl implements UserNotificationPreferenceService {
    private final UserNotificationPreferenceRepo userNotificationPreferenceRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<UserNotificationPreferenceVO> findAllByUserId(Long id) {
        return userNotificationPreferenceRepo.findAllByUserId(id).stream()
            .map(userNotificationPreference -> modelMapper.map(userNotificationPreference,
                UserNotificationPreferenceVO.class))
            .toList();
    }

    @Override
    public boolean existsByUserIdAndEmailPreferenceAndPeriodicity(EmailPreferenceDto emailPreferenceDto) {
        Long userId = emailPreferenceDto.userId();
        EmailPreference emailPreference = emailPreferenceDto.emailPreference();
        EmailPreferencePeriodicity periodicity = emailPreferenceDto.emailPreferencePeriodicity();
        return userNotificationPreferenceRepo.existsByUserIdAndEmailPreferenceAndPeriodicity(userId, emailPreference,
            periodicity);
    }
}
