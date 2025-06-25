package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.SetLocationForUserDto;
import greencity.enums.RetryableTaskType;

public class SetLocationForUserProcessor extends AbstractRetryableTaskProcessor<SetLocationForUserDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    protected SetLocationForUserProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.SET_LOCATION_FOR_USER);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<SetLocationForUserDto> getPayloadClass() {
        return SetLocationForUserDto.class;
    }

    @Override
    protected void handle(SetLocationForUserDto payload) {
        greenCityRemoteClient.setLocationForUser(payload.getId(), payload.getUserProfileDtoRequest());
    }
}
