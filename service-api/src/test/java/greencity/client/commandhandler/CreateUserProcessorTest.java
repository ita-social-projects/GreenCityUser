package greencity.client.commandhandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.CreateGreenCityUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserProcessorTest {
    @Mock
    private GreenCityRemoteClient greenCityRemoteClient;
    @InjectMocks
    private CreateUserProcessor createUserProcessor;

    @Test
    void handleCallCreateUserTest() {
        CreateGreenCityUserDto dto = new CreateGreenCityUserDto();
        createUserProcessor.handle(dto);
        verify(greenCityRemoteClient, times(1)).createUser(dto);
    }

    @Test
    void getPayloadClassTest() {
        Assertions.assertEquals(CreateGreenCityUserDto.class, createUserProcessor.getPayloadClass());
    }

}
