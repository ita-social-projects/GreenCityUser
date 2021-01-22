package greencity.client;

import greencity.constant.RestTemplateLinks;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.IMAGES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Value("${greencity.server.address}")
    private String greenCityServerAddress;
    @InjectMocks
    private RestClient restClient;

    @Test
    void getAllAvailableCustomGoals() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long userId = 1L;
        CustomGoalResponseDto customGoalResponseDto = new CustomGoalResponseDto(1L, "test");
        CustomGoalResponseDto[] customGoalResponseDtos = new CustomGoalResponseDto[1];
        customGoalResponseDtos[0] = customGoalResponseDto;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.CUSTOM_GOALS + userId, HttpMethod.GET, entity, CustomGoalResponseDto[].class))
                .thenReturn(ResponseEntity.ok(customGoalResponseDtos));

        assertEquals(Arrays.asList(customGoalResponseDtos), restClient.getAllAvailableCustomGoals(userId));
    }

    @Test
    void convertToMultipartImage() {
        MultipartFile image = new MockMultipartFile("data", "filename.png",
            "image/png", "some xml".getBytes());
        String profilePicturePath = "profilePicturePath";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.FILES_CONVERT + RestTemplateLinks.IMAGE
            + profilePicturePath, HttpMethod.POST, entity, MultipartFile.class))
                .thenReturn(ResponseEntity.ok(image));
        assertEquals(image, restClient.convertToMultipartImage(profilePicturePath));
    }

    @Test
    void uploadImage() {
        String imagePath = "image";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        MultipartFile image = new MockMultipartFile("data", "filename.png",
            "image/png", "some xml".getBytes());
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(IMAGES, image);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.postForObject(greenCityServerAddress
            + RestTemplateLinks.FILES_IMAGE, requestEntity, String.class)).thenReturn(imagePath);
        assertEquals(imagePath, restClient.uploadImage(image));
    }

    @Test
    void deleteSocialNetwork() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long socialNetworkId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS + RestTemplateLinks.ID + socialNetworkId,
            HttpMethod.DELETE, entity, Long.class)).thenReturn(ResponseEntity.ok(socialNetworkId));
        assertEquals(socialNetworkId, restClient.deleteSocialNetwork(socialNetworkId));
    }

    @Test
    void getSocialNetworkImageByUrl() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "http:";
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath("test");
        socialNetworkImageVO.setImagePath("http:");
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.SOCIAL_NETWORKS_IMAGE + RestTemplateLinks.URL + url,
            HttpMethod.GET, entity, SocialNetworkImageVO.class)).thenReturn(ResponseEntity.ok(socialNetworkImageVO));
        assertEquals(socialNetworkImageVO, restClient.getSocialNetworkImageByUrl(url));
    }

    @Test
    void findAmountOfPublishedNews() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long publishedNews = 5L;
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.ECONEWS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity, Long.class))
                .thenReturn(ResponseEntity.ok(publishedNews));
        assertEquals(publishedNews, restClient.findAmountOfPublishedNews(userId));
    }

    @Test
    void findAmountOfWrittenTipsAndTrick() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long writtenTipsAndTrick = 5L;
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.TIPSANDTRICKS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET, entity,
            Long.class)).thenReturn(ResponseEntity.ok(writtenTipsAndTrick));
        assertEquals(writtenTipsAndTrick, restClient.findAmountOfWrittenTipsAndTrick(userId));
    }

    @Test
    void findAmountOfAcquiredHabits() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long acquiredHabits = 5L;
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.HABIT_STATISTIC_ACQUIRED_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
            entity, Long.class)).thenReturn(ResponseEntity.ok(acquiredHabits));
        assertEquals(acquiredHabits, restClient.findAmountOfAcquiredHabits(userId));
    }

    @Test
    void findAmountOfHabitsInProgress() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long habitsInProgress = 5L;
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.HABIT_STATISTIC_IN_PROGRESS_COUNT + RestTemplateLinks.USER_ID + userId, HttpMethod.GET,
            entity, Long.class)).thenReturn(ResponseEntity.ok(habitsInProgress));
        assertEquals(habitsInProgress, restClient.findAmountOfHabitsInProgress(userId));
    }

    @Test
    void getAllLanguageCodes() {
        String[] allLanguageCodes = new String[3];
        allLanguageCodes[0] = "en";
        allLanguageCodes[1] = "uk";
        allLanguageCodes[2] = "ru";
        when(restTemplate.getForObject(greenCityServerAddress
            + RestTemplateLinks.LANGUAGE, String[].class)).thenReturn(allLanguageCodes);

        assertEquals(Arrays.asList(allLanguageCodes), restClient.getAllLanguageCodes());
    }
}
