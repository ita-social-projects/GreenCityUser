package greencity.client;

import greencity.constant.RestTemplateLinks;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.IMAGES;

@RequiredArgsConstructor
@Component
public class RestClient {
    private final RestTemplate restTemplate;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    private final HttpServletRequest httpServletRequest;

    /**
     * Method for finding all custom goals.
     *
     * @param userId of {@link UserVO}
     * @return list of {@link CustomGoalResponseDto}
     * @author Orest Mamchuk
     */
    public List<CustomGoalResponseDto> getAllAvailableCustomGoals(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<CustomGoalResponseDto[]> exchange = restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.CUSTOM_GOALS + userId, HttpMethod.GET, entity, CustomGoalResponseDto[].class);
        CustomGoalResponseDto[] responseDtos = exchange.getBody();
        assert responseDtos != null;
        return Arrays.asList(responseDtos);
    }

    /**
     * Method for convert image to multipart image.
     *
     * @param profilePicturePath link to image
     * @return MultipartFile
     * @author Orest Mamchuk
     */
    public MultipartFile convertToMultipartImage(String profilePicturePath) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.FILES_CONVERT + RestTemplateLinks.IMAGE
            + profilePicturePath,
            HttpMethod.POST, entity, MultipartFile.class).getBody();
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
        map.add(IMAGES, image);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, setHeader());
        return restTemplate.postForObject(greenCityServerAddress
            + RestTemplateLinks.FILES_IMAGE, requestEntity, String.class);
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
            + RestTemplateLinks.ECONEWS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity, Long.class)
            .getBody();
    }

    /**
     * The method find count of published tip&tricks.
     *
     * @param userId of {@link UserVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfWrittenTipsAndTrick(Long userId) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.TIPSANDTRICKS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity,
            Long.class).getBody();
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
