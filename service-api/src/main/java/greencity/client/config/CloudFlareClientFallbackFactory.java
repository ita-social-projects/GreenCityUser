package greencity.client.config;

import greencity.client.CloudFlareClient;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.RemoteServerUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFlareClientFallbackFactory implements FallbackFactory<CloudFlareClient> {
    @Override
    public CloudFlareClient create(Throwable cause) {
        return request -> {
            throw new RemoteServerUnavailableException(ErrorMessage.COULD_NOT_RETRIEVE_CHECKOUT_RESPONSE, cause);
        };
    }
}
