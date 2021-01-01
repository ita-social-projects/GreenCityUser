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

import java.util.Arrays;
import java.util.List;

import static greencity.constant.AppConstant.IMAGES;

@RequiredArgsConstructor
@Component
public class RestClient {
    private final RestTemplate restTemplate;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;

    /**
     * Method for finding all custom goals.
     *
     * @param userId of {@link UserVO}
     * @param entity {@link HttpEntity}
     * @return list of {@link CustomGoalResponseDto}
     * @author Orest Mamchuk
     */
    public List<CustomGoalResponseDto> getAllAvailableCustomGoals(Long userId, HttpEntity<String> entity) {
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
     * @param entity             {@link HttpEntity}
     * @return MultipartFile
     * @author Orest Mamchuk
     */
    public MultipartFile convertToMultipartImage(String profilePicturePath, HttpEntity<String> entity) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.FILES_CONVERT + RestTemplateLinks.IMAGE
            + profilePicturePath,
            HttpMethod.POST, entity, MultipartFile.class).getBody();
    }

    /**
     * Method for uploading an image.
     *
     * @param image   {@link MultipartFile}
     * @param headers {@link HttpHeaders}
     * @return String
     * @author Orest Mamchuk
     */
    public String uploadImage(MultipartFile image, HttpHeaders headers) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(IMAGES, image);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(greenCityServerAddress
            + RestTemplateLinks.FILES_IMAGE, requestEntity, String.class);
    }

    /**
     * Method for delete social network by id.
     *
     * @param entity          {@link HttpEntity}
     * @param socialNetworkId of {@link SocialNetworkImageVO}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long deleteSocialNetwork(HttpEntity<String> entity, Long socialNetworkId) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS + RestTemplateLinks.ID + socialNetworkId,
            HttpMethod.DELETE, entity, Long.class).getBody();
    }

    /**
     * Method for finding social network image.
     *
     * @param entity {@link HttpEntity}
     * @param url    social network image url
     * @return {@link SocialNetworkImageVO}
     * @author Orest Mamchuk
     */
    public SocialNetworkImageVO getSocialNetworkImageByUrl(HttpEntity<String> entity, String url) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS_IMAGE + RestTemplateLinks.URL + url,
            HttpMethod.GET, entity, SocialNetworkImageVO.class).getBody();
    }

    /**
     * The method find count of published eco news.
     *
     * @param userId of {@link UserVO}
     * @param entity {@link HttpEntity}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfPublishedNews(Long userId, HttpEntity<String> entity) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.ECONEWS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity, Long.class)
            .getBody();
    }

    /**
     * The method find count of published tip&tricks.
     *
     * @param userId of {@link UserVO}
     * @param entity {@link HttpEntity}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfWrittenTipsAndTrick(Long userId, HttpEntity<String> entity) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.TIPSANDTRICKS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity,
            Long.class).getBody();
    }

    /**
     * Method for getting amount of acquired habit by {@link UserVO} id.
     *
     * @param userId of {@link UserVO}
     * @param entity {@link HttpEntity}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfAcquiredHabits(Long userId, HttpEntity<String> entity) {
        return restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.HABIT_STATISTIC_ACQUIRED_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
            entity, Long.class).getBody();
    }

    /**
     * Method for getting amount of in progress habit by {@link UserVO} id.
     *
     * @param userId of {@link UserVO}
     * @param entity {@link HttpEntity}
     * @return Long
     * @author Orest Mamchuk
     */
    public Long findAmountOfHabitsInProgress(Long userId, HttpEntity<String> entity) {
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
}
