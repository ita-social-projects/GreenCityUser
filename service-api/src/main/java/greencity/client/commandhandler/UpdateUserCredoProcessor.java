package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserCredoProcessor extends AbstractRetryableTaskProcessor<UpdateUserCredoDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    public UpdateUserCredoProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_USER_CREDO);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UpdateUserCredoDto> getPayloadClass() {
        return UpdateUserCredoDto.class;
    }

    @Override
    protected void handle(UpdateUserCredoDto payload) {
        greenCityRemoteClient.updateUserCredo(payload.userId(), payload.userCredo());
    }
}
