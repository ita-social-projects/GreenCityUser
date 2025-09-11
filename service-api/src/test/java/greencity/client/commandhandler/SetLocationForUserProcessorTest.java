package greencity.client.commandhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.SetLocationForUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SetLocationForUserProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private SetLocationForUserProcessor processor;

    @Test
    void getPayloadClass() {
        assertEquals(SetLocationForUserDto.class, processor.getPayloadClass());
    }

    @Test
    void handle() {
        SetLocationForUserDto dto = new SetLocationForUserDto();
        dto.setId(1L);
        processor.handle(dto);
        verify(greenCityRemoteClient, times(1)).setLocationForUser(1L, dto.getUserProfileDtoRequest());
    }
}