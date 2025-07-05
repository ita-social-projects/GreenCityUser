package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserNameDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserNameProcessor extends AbstractRetryableTaskProcessor<UpdateUserNameDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    protected UpdateUserNameProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_USERNAME);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UpdateUserNameDto> getPayloadClass() {
        return UpdateUserNameDto.class;
    }

    @Override
    protected void handle(UpdateUserNameDto payload) {
        greenCityRemoteClient.updateUserName(payload.getId(), payload.getName());
    }
}
