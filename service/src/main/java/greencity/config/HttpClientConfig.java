package greencity.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {
    /**
     * Method create HttpClient.
     *
     * @return {@link CloseableHttpClient}
     */
    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
