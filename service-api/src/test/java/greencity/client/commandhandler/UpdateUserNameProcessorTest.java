package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.SetLocationForUserDto;
import greencity.dto.user.UpdateUserNameDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserNameProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private UpdateUserNameProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(UpdateUserNameDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        UpdateUserNameDto dto = new UpdateUserNameDto();
        dto.setId(1L);
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).updateUserName(dto.getId(), dto.getName());
    }
}