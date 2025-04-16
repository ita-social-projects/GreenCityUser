package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.GreenCityRemoteClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreenCityRemoteClientFallbackFactory implements FallbackFactory<GreenCityRemoteClient> {
    @Override
    public GreenCityRemoteClient create(Throwable throwable) {
        return new GreenCityRemoteClient() {

        };
    }
}
