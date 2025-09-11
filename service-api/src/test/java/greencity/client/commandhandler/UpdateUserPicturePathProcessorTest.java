package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserPicturePathDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserPicturePathProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private UpdateUserPicturePathProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(UpdateUserPicturePathDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        UpdateUserPicturePathDto dto = new UpdateUserPicturePathDto();
        dto.setUserId(1L);
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).updateUserPicturePath(dto.getUserId(), dto.getPicturePath());
    }
}