package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserEmailDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserEmailProcessor extends AbstractRetryableTaskProcessor<UpdateUserEmailDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    protected UpdateUserEmailProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_EMAIL);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UpdateUserEmailDto> getPayloadClass() {
        return UpdateUserEmailDto.class;
    }

    @Override
    protected void handle(UpdateUserEmailDto payload) {
        greenCityRemoteClient.updateUserEmail(payload.getId(), payload.getNewEmail());
    }
}
