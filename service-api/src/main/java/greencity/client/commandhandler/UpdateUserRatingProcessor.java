package greencity.client.commandhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.AbstractRetryableTaskProcessor;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.user.UserAddRatingDto;
import greencity.enums.RetryableTaskType;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserRatingProcessor extends AbstractRetryableTaskProcessor<UserAddRatingDto> {
    private final GreenCityRemoteClient greenCityRemoteClient;

    public UpdateUserRatingProcessor(ObjectMapper objectMapper,
        GreenCityRemoteClient greenCityRemoteClient) {
        super(objectMapper, RetryableTaskType.UPDATE_USER_RATING);
        this.greenCityRemoteClient = greenCityRemoteClient;
    }

    @Override
    protected Class<UserAddRatingDto> getPayloadClass() {
        return UserAddRatingDto.class;
    }

    @Override
    protected void handle(UserAddRatingDto payload) {
        greenCityRemoteClient.updateUserRating(payload);
    }
}
