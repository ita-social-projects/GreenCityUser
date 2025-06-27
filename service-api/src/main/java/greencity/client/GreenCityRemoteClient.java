package greencity.client;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserProfileDtoRequest;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GreenCityRemoteClient {
    private final WebClient webClient;

    private static final String USER_ID_QUERY_PARAM = "userId";
    private static final String PROFILE_PICTURE_PATH_QUERY_PARAM = "profilePicturePath";

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
        MultipartFile[] multipartFiles = files.toArray(new MultipartFile[0]);

        return webClient.post()
            .uri("/files")
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
        return webClient.post()
            .uri("/files/single")
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
        webClient.method(HttpMethod.DELETE)
            .uri("/files")
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
        try {
            return webClient.get()
                .uri("/achievements/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AchievementVO>>() {
                })
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve all achievements: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Method returns all user achievements by user id.
     *
     * @param userId id of the user
     * @return list of {@link UserAchievementVO}
     */
    public List<UserAchievementVO> findAllUserAchievementsByUserId(Long userId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/achievements/user-achievements/{userId}").build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserAchievementVO>>() {
                })
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve all achievements by user id: {}",
                e.getMessage());
            return List.of();
        }
    }

    /**
     * Method to find {@link UserCityDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserCityDto}.
     */
    public UserCityDto findAllUsersCities(Long userId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}/cities").build(userId))
                .retrieve()
                .bodyToMono(UserCityDto.class)
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve all users cities: {}",
                e.getMessage());
            return null;
        }
    }

    /**
     * Method to find {@link UserLocationDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}.
     */
    // TODO: seems like it is no longer used
    public Optional<UserLocationDto> findUserLocationByUserId(Long userId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}/location").build(userId))
                .retrieve()
                .onStatus(
                    httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
                    clientResponse -> Mono.empty())
                .bodyToMono(UserLocationDto.class)
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()))
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve location bu user id: {}",
                e.getMessage());
            return Optional.of(new UserLocationDto());
        }
    }

    /**
     * Method to update user location by user id.
     *
     * @param userId                id of the user
     * @param userProfileDtoRequest contains location data
     */
    public void setLocationForUser(Long userId, UserProfileDtoRequest userProfileDtoRequest) {
        webClient.patch()
            .uri(uriBuilder -> uriBuilder.path("/users/{userId}/location").build(userId))
            .bodyValue(userProfileDtoRequest)
            .retrieve()
            .bodyToMono(Void.class)
            .subscribe();
    }

    /**
     * Get all user's friends ids by user id.
     *
     * @param userId id of the user.
     * @return list of friends ids.
     */
    public List<Long> getAllUserFriendsIds(Long userId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}/all-friends").build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
                })
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve all user friends ids: {}",
                e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all user friends ids as a page.
     *
     * @param userId   id of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    public PageableAdvancedDto<Long> getAllUserFriendsIds(Long userId, Pageable pageable) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}/friends")
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageableAdvancedDto<Long>>() {
                })
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve pageable all user friends ids: {}",
                e.getMessage());
            return null;
        }
    }

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of friends ids
     */
    public List<Long> getSixFriendsIdsWithTheHighestRating(Long userId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}/top-friends").build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
                })
                .block();
        } catch (WebClientRequestException e) {
            log.warn("GreenCity service is unavailable, failed to retrieve friends ids with the highest rating: {}",
                e.getMessage());
            return List.of();
        }
    }

    /**
     * Increase user rating by amount specified in {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    public void updateUserRating(UserAddRatingDto userAddRatingDto) {
        webClient.patch()
            .uri("/users/rating")
            .bodyValue(userAddRatingDto)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Update user credo by user id.
     *
     * @param userId    user id
     * @param userCredo new user credo
     **/
    public void updateUserCredo(Long userId, String userCredo) {
        UpdateUserCredoDto updateUserCredoDto = new UpdateUserCredoDto(userId, userCredo);

        webClient.patch()
            .uri("/users/credo")
            .bodyValue(updateUserCredoDto)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Sends a request to the GreenCity service to create a new user.
     *
     * @param createUserDto the data transfer object containing user creation
     *                      information
     * @return {@code true} if the user was successfully created, {@code false}
     *         otherwise
     */
    public boolean createUser(CreateGreenCityUserDto createUserDto) {
        return Boolean.TRUE.equals(webClient.post()
            .uri("/users/create")
            .bodyValue(createUserDto)
            .retrieve()
            .bodyToMono(Boolean.class)
            .block());
    }

    /**
     * Updates the user's profile picture path in the GreenCity service.
     *
     * @param userId             the ID of the user whose picture path should be
     *                           updated
     * @param profilePicturePath the new profile picture path to be set
     */
    public void updateUserPicturePath(Long userId, String profilePicturePath) {
        webClient.put()
            .uri(uriBuilder -> uriBuilder.path("/users/picturePath")
                .queryParam(USER_ID_QUERY_PARAM, userId)
                .queryParam(PROFILE_PICTURE_PATH_QUERY_PARAM, profilePicturePath)
                .build())
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Updates the user's name in the GreenCity service.
     *
     * @param userId   the ID of the user whose name should be updated
     * @param userName the new name to assign to the user
     */
    public void updateUserName(Long userId, String userName) {
        webClient.patch()
            .uri(uriBuilder -> uriBuilder.path("/users/{userId}/name")
                .queryParam("userName", userName)
                .build(userId))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Retrieves a list of user profile information from the GreenCity service for
     * the given list of user IDs.
     *
     * @param userIds list of user IDs to fetch profile data for
     * @return a list of {@link GreenCityUserProfileDtoResponse} objects containing
     *         user profile information
     */
    public List<GreenCityUserProfileDtoResponse> findGreenCityUserProfilesByUserIds(List<Long> userIds) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/users/profiles")
                .queryParam("userIds", userIds)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<GreenCityUserProfileDtoResponse>>() {
            })
            .block();
    }

    public GreenCityUserProfileDtoResponse findGreenCityUserProfileByUserId(Long userId) {
        var greenCityUserProfiles = findGreenCityUserProfilesByUserIds(List.of(userId));
        return greenCityUserProfiles.getFirst();
    }

    private BodyInserters.MultipartInserter multipartInserter(MultipartFile... multipartFiles) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        for (MultipartFile multipartFile : multipartFiles) {
            multipartBodyBuilder.part("file", multipartFile.getResource());
        }

        return BodyInserters.fromMultipartData(multipartBodyBuilder.build());
    }
}
