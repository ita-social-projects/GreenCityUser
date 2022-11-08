package greencity.client;

import greencity.constant.RestTemplateLinks;
import greencity.dto.shoppinglist.CustomShoppingListItemResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.IMAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @InjectMocks
    private RestClient restClient;

    @Test
    void calculateAchievement() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress + RestTemplateLinks.CALCULATE_ACHIEVEMENT
            + RestTemplateLinks.CALCULATE_ACHIEVEMENT_ID + 1L
            + RestTemplateLinks.CALCULATE_ACHIEVEMENT_SETTER + AchievementType.INCREMENT
            + RestTemplateLinks.CALCULATE_ACHIEVEMENT_SOCIAL_NETWORK + AchievementCategoryType.ECO_NEWS
            + RestTemplateLinks.CALCULATE_ACHIEVEMENT_SIZE + 1,
            HttpMethod.POST, entity, Object.class)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        assertEquals(ResponseEntity.status(HttpStatus.OK).build(),
            restClient.calculateAchievement(1L, AchievementType.INCREMENT, AchievementCategoryType.ECO_NEWS, 1));
    }

    @Test
    void getAllAvailableCustomShoppingListItems() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long userId = 1L;
        Long habitId = 1L;
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            new CustomShoppingListItemResponseDto(1L, "test");
        CustomShoppingListItemResponseDto[] customShoppingListItemResponseDtos =
            new CustomShoppingListItemResponseDto[1];
        customShoppingListItemResponseDtos[0] = customShoppingListItemResponseDto;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.CUSTOM_SHOPPING_LIST_ITEMS + userId + "/" + habitId, HttpMethod.GET, entity,
            CustomShoppingListItemResponseDto[].class))
                .thenReturn(ResponseEntity.ok(customShoppingListItemResponseDtos));

        assertEquals(Arrays.asList(customShoppingListItemResponseDtos),
            restClient.getAllAvailableCustomShoppingListItems(userId, habitId));
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
    void uploadImage() throws IOException {
        String imagePath = "image";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultipartFile image =
            new MockMultipartFile("data", "filename.png", "image/png",
                "some xml".getBytes());
        ByteArrayResource fileAsResource = new ByteArrayResource(image.getBytes()) {

            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(IMAGE, fileAsResource);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.postForObject(greenCityServerAddress +
            RestTemplateLinks.FILES_IMAGE, requestEntity,
            String.class)).thenReturn(imagePath);
        assertEquals(imagePath,
            restClient.uploadImage(image));
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

    @Test
    void addUserToSystemChat() {
        Long userId = 1L;
        ResponseEntity<Long> responseEntity = ResponseEntity.ok().body(userId);
        when(restTemplate.postForEntity(greenCityChatServerAddress + "/chat/user", 1L, Long.class))
            .thenReturn(responseEntity);
        restClient.addUserToSystemChat(userId);
        verify(restTemplate).postForEntity(greenCityChatServerAddress + "/chat/user", 1L, Long.class);

    }
}
