package greencity.client;

import greencity.constant.RestTemplateLinks;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static greencity.constant.AppConstant.IMAGES;

@RequiredArgsConstructor
@Component
public class RestClient {
    private final RestTemplate restTemplate;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;

    public CustomGoalResponseDto[] getAllAvailableCustomGoals(Long userId, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                + RestTemplateLinks.CUSTOM_GOALS + userId, HttpMethod.GET, entity, CustomGoalResponseDto[].class)
                .getBody();
    }

    public MultipartFile convertToMultipartImage(String profilePicturePath, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.FILES_CONVERT + RestTemplateLinks.IMAGE
                        + profilePicturePath,
                HttpMethod.POST, entity, MultipartFile.class).getBody();
    }

    public String uploadImage(MultipartFile image, HttpHeaders headers){
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(IMAGES, image);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(greenCityServerAddress
                + RestTemplateLinks.FILES_IMAGE, requestEntity, String.class);
    }

    public Long deleteSocialNetwork(HttpEntity<String>entity, Long socialNetworkId){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.SOCIAL_NETWORKS + RestTemplateLinks.ID + socialNetworkId,
                HttpMethod.DELETE, entity, Long.class).getBody();
    }

    public SocialNetworkImageVO getSocialNetworkImageByUrl(HttpEntity<String>entity, String url){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.SOCIAL_NETWORKS_IMAGE + RestTemplateLinks.URL + url,
                HttpMethod.GET, entity, SocialNetworkImageVO.class).getBody();
    }

    public Long findAmountOfPublishedNews(Long userId, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                + RestTemplateLinks.ECONEWS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity, Long.class)
                .getBody();
    }

    public Long findAmountOfWrittenTipsAndTrick(Long userId, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.TIPSANDTRICKS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity,
                Long.class).getBody();
    }

    public Long findAmountOfAcquiredHabits(Long userId, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.HABIT_STATISTIC_ACQUIRED_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
                entity, Long.class).getBody();
    }

    public Long findAmountOfHabitsInProgress(Long userId, HttpEntity<String>entity){
        return restTemplate.exchange(greenCityServerAddress
                        + RestTemplateLinks.HABIT_STATISTIC_IN_PROGRESS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
                entity, Long.class).getBody();
    }
}
