package greencity.client;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.FILES;
import greencity.constant.RestTemplateLinks;
import greencity.dto.friends.FriendsChatDto;
import greencity.dto.todolist.CustomToDoListItemResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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
    void getAllAvailableCustomToDoListItems() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long userId = 1L;
        Long habitId = 1L;
        CustomToDoListItemResponseDto customToDoListItemResponseDto =
            new CustomToDoListItemResponseDto(1L, "test");
        CustomToDoListItemResponseDto[] customToDoListItemResponseDtos =
            new CustomToDoListItemResponseDto[1];
        customToDoListItemResponseDtos[0] = customToDoListItemResponseDto;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.CUSTOM_TO_DO_LIST_ITEMS + userId + "/" + habitId, HttpMethod.GET, entity,
            CustomToDoListItemResponseDto[].class))
                .thenReturn(ResponseEntity.ok(customToDoListItemResponseDtos));

        assertEquals(Arrays.asList(customToDoListItemResponseDtos),
            restClient.getAllAvailableCustomToDoListItems(userId, habitId));
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
        map.add(FILES, fileAsResource);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.postForObject(greenCityServerAddress +
            RestTemplateLinks.FILES, requestEntity,
            String.class)).thenReturn(imagePath);
        assertEquals(imagePath,
            restClient.uploadImage(image));
        verify(httpServletRequest).getHeader(any());
        verify(restTemplate).postForObject(greenCityServerAddress +
            RestTemplateLinks.FILES, requestEntity,
            String.class);
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
        when(restTemplate.exchange(
            greenCityServerAddress + RestTemplateLinks.ECO_NEWS_COUNT + RestTemplateLinks.AUTHOR_ID + userId,
            HttpMethod.GET, entity, Long.class))
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
    void createUbsProfileTest() {
        UbsProfileCreationDto ubsProfileCreationDto =
            UbsProfileCreationDto.builder()
                .uuid("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")
                .email("ubsemail@mail.com")
                .name("UBS")
                .build();
        ResponseEntity<Long> responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(1L);
        when(restTemplate.postForEntity(greenCityUbsServerAddress + RestTemplateLinks.UBS_USER_PROFILE + "/user/create",
            ubsProfileCreationDto, Long.class)).thenReturn(responseEntity);
        Long id = restClient.createUbsProfile(ubsProfileCreationDto);
        verify(restTemplate, times(1)).postForEntity(
            greenCityUbsServerAddress + RestTemplateLinks.UBS_USER_PROFILE + "/user/create",
            ubsProfileCreationDto, Long.class);
        assertEquals(1L, id);
    }

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

    @Test
    void findAmountOfEventsAttendedByUserTest() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.EVENTS_ATTENDED_COUNT_BY_USER_ID + userId,
            HttpMethod.GET, entity, Long.class))
                .thenReturn(ResponseEntity.ok(1L));

        assertEquals(1, restClient.findAmountOfEventsAttendedByUser(userId));

        verify(httpServletRequest).getHeader(AUTHORIZATION);
        verify(restTemplate).exchange(greenCityServerAddress
            + RestTemplateLinks.EVENTS_ATTENDED_COUNT_BY_USER_ID + userId,
            HttpMethod.GET, entity, Long.class);
    }

    @Test
    void findAmountOfEventsOrganizedByUserTest() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long userId = 1L;
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityServerAddress
            + RestTemplateLinks.EVENTS_ORGANIZED_COUNT_BY_USER_ID + userId,
            HttpMethod.GET, entity, Long.class))
                .thenReturn(ResponseEntity.ok(1L));

        assertEquals(1, restClient.findAmountOfEventsOrganizedByUser(userId));

        verify(httpServletRequest).getHeader(AUTHORIZATION);
        verify(restTemplate).exchange(greenCityServerAddress
            + RestTemplateLinks.EVENTS_ORGANIZED_COUNT_BY_USER_ID + userId,
            HttpMethod.GET, entity, Long.class);
    }
}
