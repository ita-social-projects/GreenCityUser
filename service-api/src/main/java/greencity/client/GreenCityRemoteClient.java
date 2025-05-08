package greencity.client;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.user.UpdateUserDto;
import greencity.dto.user.UserDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.useraction.UserActionVO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import greencity.dto.user.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class GreenCityRemoteClient {
    private final WebClient webClient;

    public GreenCityRemoteClient(
        @Qualifier("greenCityWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Method for uploading files.
     *
     * @param files files to save.
     * @return urls of the saved files.
     */
    public List<String> uploadAllFiles(List<MultipartFile> files) {
        String path = "/files";

        MultipartFile[] multipartFiles = files.toArray(new MultipartFile[0]);

        return webClient.post()
            .uri(path)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(multipartInserter(multipartFiles))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {
            })
            .block();
    }

    /**
     * Method for uploading a file.
     *
     * @param file file to save.
     * @return url of the saved file.
     */
    public String uploadFile(MultipartFile file) {
        String path = "/files/single";

        return webClient.post()
            .uri(path)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(multipartInserter(file))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    /**
     * Method for deleting files.
     *
     * @param paths urls of files to delete.
     */
    public void deleteAllFiles(List<String> paths) {
        String path = "/files";

        webClient.method(HttpMethod.DELETE)
            .uri(path)
            .bodyValue(paths)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Method returns all achievements.
     *
     * @return list of {@link AchievementVO}
     */
    public List<AchievementVO> findAllAchievements() {
        String path = "/achievements/all";

        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<AchievementVO>>() {
            })
            .block();
    }

    /**
     * Method returns all user achievements by user id.
     *
     * @param userId id of the user
     * @return list of {@link UserAchievementVO}
     */
    public List<UserAchievementVO> findAllUserAchievementsByUserId(Long userId) {
        String path = "/achievements/user-achievements/{userId}";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserAchievementVO>>() {
            })
            .block();
    }

    /**
     * Method returns all user actions by user id.
     *
     * @param userId id of the user
     * @return list of {@link UserActionVO}
     */
    public List<UserActionVO> findAllUserActionsByUserId(Long userId) {
        String path = "/achievements/user-actions/{userId}";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserActionVO>>() {
            })
            .block();
    }

    /**
     * Method to find {@link UserCityDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserCityDto}.
     */
    public UserCityDto findAllUsersCities(Long userId) {
        String path = "/users/{userId}/cities";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .bodyToMono(UserCityDto.class)
            .block();
    }

    /**
     * Method to find {@link UserLocationDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}.
     */
    public Optional<UserLocationDto> findUserLocationByUserId(Long userId) {
        String path = "/users/{userId}/location";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .onStatus(
                httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
                clientResponse -> Mono.empty())
            .bodyToMono(UserLocationDto.class)
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty()))
            .block();
    }

    /**
     * Method to update user location by user id.
     *
     * @param userId                id of the user
     * @param userProfileDtoRequest contains location data
     */
    public void setLocationForUser(Long userId, UserProfileDtoRequest userProfileDtoRequest) {
        String path = "/users/{userId}/location";

        webClient.patch()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .bodyValue(userProfileDtoRequest)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Get all user's friends ids by user id.
     *
     * @param userId id of the user.
     * @return list of friends ids.
     */
    public List<Long> getAllUserFriendsIds(Long userId) {
        String path = "/users/{userId}/all-friends";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
            })
            .block();
    }

    /**
     * Get all user friends ids as a page.
     *
     * @param userId   id of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    public Page<Long> getAllUserFriendsIds(Long userId, Pageable pageable) {
        String path = "/users/{userId}/friends";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build(userId))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Page<Long>>() {
            })
            .block();
    }

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of friends ids
     */
    public List<Long> getSixFriendsIdsWithTheHighestRating(Long userId) {
        String path = "/users/{userId}/top-friends";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
            })
            .block();
    }

    /**
     * Increase user rating by amount specified in {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    public void updateUserRating(UserAddRatingDto userAddRatingDto) {
        String path = "/users/rating";

        webClient.patch()
            .uri(path)
            .bodyValue(userAddRatingDto)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Synchronize GreenCityUser and GreenCity user entity via update.
     *
     * @param updateUserDto {@link UpdateUserDto} contains data for PATCH-update.
     */
    public boolean updateUser(UpdateUserDto updateUserDto) {
        String path = "/users/update";

        return webClient.patch()
            .uri(path)
            .bodyValue(updateUserDto)
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }

    private BodyInserters.MultipartInserter multipartInserter(MultipartFile... multipartFiles) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        for (MultipartFile multipartFile : multipartFiles) {
            multipartBodyBuilder.part("file", multipartFile.getResource());
        }

        return BodyInserters.fromMultipartData(multipartBodyBuilder.build());
    }

    public boolean createUser(UserDto createUserDto) {
        String path = "/users/create";

        return Boolean.TRUE.equals(webClient.post()
            .uri(path)
            .bodyValue(createUserDto)
            .retrieve()
            .bodyToMono(Boolean.class)
            .block());
    }
}
