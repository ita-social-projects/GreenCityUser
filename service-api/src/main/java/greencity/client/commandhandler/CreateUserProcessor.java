package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.GreenCityRemoteClient;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class CreateUserProcessor extends AbstractRetryableTaskProcessor<CreateGreenCityUserDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    public CreateUserProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.CREATE_USER);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<CreateGreenCityUserDto> getPayloadClass() {
        return CreateGreenCityUserDto.class;
    }

    @Override
    protected void handle(CreateGreenCityUserDto payload) {
        greenCityRemoteClient.createUser(payload);
    }
}
