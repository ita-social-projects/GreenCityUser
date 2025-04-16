package greencity.client;

import greencity.client.config.GreenCityRemoteClientFallbackFactory;
import greencity.client.config.GreenCityRemoteClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "greencity-remote-client",
        url = "${greencity.server.address}",
        configuration = GreenCityRemoteClientInterceptor.class,
        fallbackFactory = GreenCityRemoteClientFallbackFactory.class)
public interface GreenCityRemoteClient {
}
