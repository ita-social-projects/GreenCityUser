package greencity.service;

import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserNotificationPreferenceVO;

import java.util.List;

public interface UserNotificationPreferenceService {

    List<UserNotificationPreferenceVO> findAllByUserId(Long id);

    boolean existsByUserIdAndEmailPreferenceAndPeriodicity(EmailPreferenceDto emailPreferenceDto);
}
