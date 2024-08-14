package greencity.client;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.FILES;
import greencity.constant.RestTemplateLinks;
import greencity.dto.friends.FriendsChatDto;
import greencity.dto.shoppinglist.CustomShoppingListItemResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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
     * Method for finding all custom shopping list items.
     *
     * @param userId of {@link UserVO}
     * @return list of {@link CustomShoppingListItemResponseDto}
     * @author Orest Mamchuk
     */
    public List<CustomShoppingListItemResponseDto> getAllAvailableCustomShoppingListItems(Long userId, Long habitId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<CustomShoppingListItemResponseDto[]> exchange = restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.CUSTOM_SHOPPING_LIST_ITEMS + userId + "/" + habitId, HttpMethod.GET, entity,
            CustomShoppingListItemResponseDto[].class);
        CustomShoppingListItemResponseDto[] responseDtos = exchange.getBody();
        assert responseDtos != null;
        return Arrays.asList(responseDtos);
    }

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
     * Method for delete social network by id.
     *
     * @param socialNetworkId of {@link SocialNetworkImageVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long deleteSocialNetwork(Long socialNetworkId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS + RestTemplateLinks.ID + socialNetworkId,
            HttpMethod.DELETE, entity, Long.class).getBody();
    }

    /**
     * Method for finding social network image.
     *
     * @param url social network image url
     * @return {@link SocialNetworkImageVO}
     * @author Orest Mamchuk
     */
    public SocialNetworkImageVO getSocialNetworkImageByUrl(String url) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS_IMAGE + RestTemplateLinks.URL + url,
            HttpMethod.GET, entity, SocialNetworkImageVO.class).getBody();
    }

    /**
     * The method find count of published eco news.
     *
     * @param userId of {@link UserVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfPublishedNews(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.ECO_NEWS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity, Long.class)
            .getBody();
    }

    /**
     * Method for getting amount of acquired habit by {@link UserVO} id.
     *
     * @param userId of {@link UserVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfAcquiredHabits(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.HABIT_STATISTIC_ACQUIRED_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
            entity, Long.class).getBody();
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
        FriendsChatDto body =
            restTemplate.exchange(greenCityChatServerAddress + "/chat/exist/" + firstUserId + "/" + secondUserId,
                HttpMethod.GET, entity, FriendsChatDto.class).getBody();
        assert body != null;
        return body;
    }

    /**
     * Method for getting amount of in progress habit by {@link UserVO} id.
     *
     * @param userId of {@link UserVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfHabitsInProgress(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.HABIT_STATISTIC_IN_PROGRESS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
            entity, Long.class).getBody();
    }

    /**
     * Method for finding all language code.
     *
     * @return list of {@link String}
     */
    public List<String> getAllLanguageCodes() {
        String[] restTemplateForObject = restTemplate.getForObject(greenCityServerAddress
            + RestTemplateLinks.LANGUAGE, String[].class);
        assert restTemplateForObject != null;
        return Arrays.asList(restTemplateForObject);
    }

    /**
     * Method for creating an ubs profile for a user.
     *
     * @param ubsProfile of {@link UbsProfileCreationDto};
     * @return id of ubs profile {@link Long};
     * @author Maksym Golik
     */

    public Long createUbsProfile(UbsProfileCreationDto ubsProfile) {
        return restTemplate
            .postForEntity(greenCityUbsServerAddress + RestTemplateLinks.UBS_USER_PROFILE + "/user/create",
                ubsProfile, Long.class)
            .getBody();
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

    /**
     * Method for getting amount of organized and attended events by {@link UserVO}
     * id.
     *
     * @param userId of {@link UserVO}
     * @return {@link Long} count of organized and attended by user events.
     * @author Olena Sotnik
     */
    public Long findAmountOfEventsOrganizedAndAttendedByUser(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.EVENTS_ORGANIZED_OR_ATTENDED_BY_USER_COUNT + RestTemplateLinks.USER_ID + userId,
            HttpMethod.GET, entity, Long.class).getBody();
    }
}
