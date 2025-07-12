package greencity.client;

import greencity.dto.friends.FriendsChatDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import static greencity.constant.AppConstant.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestClient {
    private final RestTemplate restTemplate;
    private final HttpServletRequest httpServletRequest;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    @Value("${greencitychat.server.address}")
    private String greenCityChatServerAddress;
    @Value("${greencityubs.server.address}")
    private String greenCityUbsServerAddress;

    /**
     * Method for checking if there is a chat between two people.
     *
     * @param firstUserId  of {Long}
     * @param secondUserId of {Long}
     * @return {FriendsChatDto}
     * @author Max Bohonko
     */
    public FriendsChatDto chatBetweenTwo(Long firstUserId, Long secondUserId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityChatServerAddress + "/chat/exist/" + firstUserId + "/" + secondUserId,
            HttpMethod.GET, entity, FriendsChatDto.class).getBody();
    }

    /**
     * Method makes headers for RestTemplate.
     *
     * @return {@link HttpEntity}
     */
    private HttpHeaders setHeader() {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        return headers;
    }
}
