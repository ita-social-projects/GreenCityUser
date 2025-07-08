package greencity.client;

import greencity.constant.RestTemplateLinks;
import greencity.dto.friends.FriendsChatDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.FILES;

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
     * Method for uploading an image.
     *
     * @param image {@link MultipartFile}
     * @return String
     * @author Orest Mamchuk
     */
    public String uploadImage(MultipartFile image) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        try {
            map.add(FILES, convert(image));
        } catch (IOException e) {
            log.info("File did not convert to ByteArrayResource");
        }
        return restTemplate.postForObject(greenCityServerAddress
            + RestTemplateLinks.FILES, requestEntity, String.class);
    }

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

    /**
     * Method convert MultipartFile to ByteArrayResource.
     *
     * @param image {@link MultipartFile}
     * @return {@link ByteArrayResource}
     */
    private ByteArrayResource convert(MultipartFile image) throws IOException {
        return new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
    }
}
