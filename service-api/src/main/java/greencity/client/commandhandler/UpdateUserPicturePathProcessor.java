package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UpdateUserPicturePathDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserPicturePathProcessor extends AbstractRetryableTaskProcessor<UpdateUserPicturePathDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    protected UpdateUserPicturePathProcessor(ObjectMapper objectMapper,
                                             GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_USER_PICTURE_PATH);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UpdateUserPicturePathDto> getPayloadClass() {
        return UpdateUserPicturePathDto.class;
    }

    @Override
    protected void handle(UpdateUserPicturePathDto payload) {
        greenCityRemoteClient.updateUserPicturePath(payload.getUserId(), payload.getPicturePath());
    }
}
