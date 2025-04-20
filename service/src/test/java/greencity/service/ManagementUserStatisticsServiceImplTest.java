package greencity.service;

import greencity.dto.user.UserLocationStatisticDto;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ManagementUserStatisticsServiceImplTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    ManagementUserStatisticsServiceImpl managementUserStatisticsService;

    @Test
    void testGetUserLocationsDistributionCity() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("City", 50L));
        when(userRepo.getUserLocationsDistributionByCity()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsService.getUserLocationsDistribution("city");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByCity();
    }

    @Test
    void testGetUserLocationsDistributionRegion() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("Dnipropetrovsk", 30L));
        when(userRepo.getUserLocationsDistributionByRegion()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsService.getUserLocationsDistribution("region");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByRegion();
    }

    @Test
    void testGetUserLocationsDistributionCountry() {
        List<UserLocationStatisticDto> mockResult = List.of(new UserLocationStatisticDto("Ukraine", 70L));
        when(userRepo.getUserLocationsDistributionByCountry()).thenReturn(mockResult);

        List<UserLocationStatisticDto> result =
            managementUserStatisticsService.getUserLocationsDistribution("country");

        assertEquals(mockResult, result);
        verify(userRepo).getUserLocationsDistributionByCountry();
    }
}
