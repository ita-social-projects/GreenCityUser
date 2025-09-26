package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserRatingProcessor extends AbstractRetryableTaskProcessor<UserAddRatingExternalDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    public UpdateUserRatingProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_USER_RATING);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UserAddRatingExternalDto> getPayloadClass() {
        return UserAddRatingExternalDto.class;
    }

    @Override
    protected void handle(UserAddRatingExternalDto payload) {
        greenCityRemoteClient.updateUserRating(payload);
    }
}
