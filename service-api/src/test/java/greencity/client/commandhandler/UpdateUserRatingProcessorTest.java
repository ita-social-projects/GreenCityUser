package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UserAddRatingExternalDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserRatingProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private UpdateUserRatingProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(UserAddRatingExternalDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        UserAddRatingExternalDto dto = new UserAddRatingExternalDto();
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).updateUserRating(dto);
    }
}