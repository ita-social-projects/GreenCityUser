package greencity.service;

import greencity.client.GreenCityRemoteClient;
import greencity.dto.achievement.AchievementVO;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @InjectMocks
    AchievementServiceImpl achievementService;

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @Test
    void findAllTest() {
        List<AchievementVO> expectedResult = List.of(mock(AchievementVO.class));

        when(greenCityRemoteClient.findAllAchievements())
                .thenReturn(expectedResult);

        List<AchievementVO> actualResult = achievementService.findAll();

        assertEquals(expectedResult, actualResult);
    }

}