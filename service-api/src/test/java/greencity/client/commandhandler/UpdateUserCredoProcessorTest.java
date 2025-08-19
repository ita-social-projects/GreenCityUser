package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserCredoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserCredoProcessorTest {
    @Mock
    GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    UpdateUserCredoProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(UpdateUserCredoDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        UpdateUserCredoDto dto = new UpdateUserCredoDto(1l, "test");
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).updateUserCredo(dto.userId(), dto.userCredo());
    }
}