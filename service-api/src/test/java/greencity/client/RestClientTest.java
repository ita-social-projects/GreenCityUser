package greencity.client;

import greencity.dto.friends.FriendsChatDto;
import jakarta.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    @Value("${greencitychat.server.address}")
    private String greenCityChatServerAddress;
    @Value("${greencityubs.server.address}")
    private String greenCityUbsServerAddress;
    @InjectMocks
    private RestClient restClient;

    @Test
    void testChatBetweenTwo() {
        Long firstUserId = 1L;
        Long secondUserId = 2L;
        FriendsChatDto expectedBody = new FriendsChatDto();

        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(FriendsChatDto.class)))
                .thenReturn(new ResponseEntity<>(expectedBody, HttpStatus.OK));

        FriendsChatDto result = restClient.chatBetweenTwo(firstUserId, secondUserId);

        assertNotNull(result);
        verify(restTemplate).exchange(any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(FriendsChatDto.class));
    }
}
