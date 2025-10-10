package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserEmailDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserEmailProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private UpdateUserEmailProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(UpdateUserEmailDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        UpdateUserEmailDto dto = new UpdateUserEmailDto();
        dto.setId(1L);
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).updateUserEmail(dto.getId(), dto.getNewEmail());
    }
}