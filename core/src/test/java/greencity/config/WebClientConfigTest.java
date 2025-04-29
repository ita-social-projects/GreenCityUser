package greencity.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebClientConfigTest {

    private final WebClientConfig webClientConfig = new WebClientConfig();

    @Test
    void facebookWebClientBean_ShouldBeCreated() {
        WebClient webClient = webClientConfig.facebookWebClient();
        assertNotNull(webClient, "WebClient bean should not be null");
    }

    @Test
    void webClientBean_ShouldBeInstanceOfFacebookWebClient() {
        WebClient webClient = webClientConfig.facebookWebClient();
        assertTrue(webClient instanceof WebClient, "Returned object should be an instance of WebClient");
    }

    @Test
    void facebookWebClientBean_ShouldNotBeNullAfterCreation() {
        WebClient webClient = webClientConfig.facebookWebClient();
        assertNotNull(webClient, "WebClient should not be null after creation");
        assertTrue(webClient.getClass().getName().contains("WebClient"), "Should be a WebClient implementation");
    }
}