package greencity.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GreenCityRemoteWebClient {

    private final WebClient webClient;

    public GreenCityRemoteWebClient(
            @Qualifier("greenCityWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }
}
