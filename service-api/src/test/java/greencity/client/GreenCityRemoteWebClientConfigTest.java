package greencity.client;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import greencity.client.config.GreenCityRemoteWebClientConfig;
import greencity.properties.EmailProperties;
import greencity.properties.RemoteWebClientProperties;
import greencity.security.jwt.JwtTool;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GreenCityRemoteWebClientConfigTest {
    @Mock
    private RemoteWebClientProperties remoteWebClientProperties;
    @Mock
    private EmailProperties emailProperties;

    static MockWebServer mockWebServer;

    @Mock
    JwtTool jwtTool;

    @InjectMocks
    GreenCityRemoteWebClientConfig config;

    WebClient webClient;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        when(jwtTool.createAccessToken(anyString(), anyList()))
            .thenReturn("mocked-jwt-token");

        when(emailProperties.getSystemEmailAddress()).thenReturn("test@greencity.com");
        when(remoteWebClientProperties.getConnectionTimeout()).thenReturn(1000);
        when(remoteWebClientProperties.getResponseTimeout()).thenReturn(1000);
        when(remoteWebClientProperties.getGreencityServerAddress())
            .thenReturn(mockWebServer.url("/").toString());

        webClient = config.webClient(WebClient.builder());
    }

    @Test
    void notFoundResponseThrowsNotFoundExceptionTest() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(404)
            .setBody("{\"message\": \"Not Found error from API\"}")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<String> result = webClient.get().uri("/").retrieve().bodyToMono(String.class);

        try {
            result.block();
            Assertions.fail("Expected NotFoundException to be thrown");
        } catch (Exception ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

            Assertions.assertEquals("NotFoundException", cause.getClass().getSimpleName());
            Assertions.assertTrue(cause.getMessage().contains("Not Found error from API"));
        }
    }

    @Test
    void badRequestResponseThrowsBadRequestExceptionTest() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(400)
            .setBody("{\"message\": \"Bad Request error from API\"}")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<String> result = webClient.get().uri("/").retrieve().bodyToMono(String.class);

        try {
            result.block();
            Assertions.fail("Expected BadRequestException to be thrown");
        } catch (Exception ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

            Assertions.assertEquals("BadRequestException", cause.getClass().getSimpleName());
            Assertions.assertTrue(cause.getMessage().contains("Bad Request error from API"));
        }
    }

    @Test
    void internalServerErrorThrowsGreenCityServiceExceptionTest() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(500)
            .setBody("{\"message\": \"Internal Server Error from API\"}")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<String> result = webClient.get().uri("/").retrieve().bodyToMono(String.class);

        try {
            result.block();
            Assertions.fail("Expected GreenCityServiceException to be thrown");
        } catch (Exception ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

            Assertions.assertEquals("GreenCityServiceException", cause.getClass().getSimpleName());
            Assertions.assertTrue(cause.getMessage().contains("Internal Server Error from API"));
        }
    }

    @Test
    void okResponseReturnsBodyTest() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody("Success")
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String response = webClient.get().uri("/").retrieve().bodyToMono(String.class).block();

        Assertions.assertEquals("Success", response);
    }
}
