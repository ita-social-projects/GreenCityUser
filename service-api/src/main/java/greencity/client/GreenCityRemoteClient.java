package greencity.client;

import greencity.client.config.GreenCityRemoteClientFallbackFactory;
import greencity.client.config.GreenCityRemoteClientInterceptor;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UpdateUserDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@FeignClient(name = "greencity-remote-client",
    url = "${greencity.server.address}",
    configuration = GreenCityRemoteClientInterceptor.class,
    fallbackFactory = GreenCityRemoteClientFallbackFactory.class)
@Component
public interface GreenCityRemoteClient {
    /**
     * Method for uploading files.
     *
     * @param files files to save.
     * @return urls of the saved files.
     */
    @PostMapping(path = "/files", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<String> uploadAllFiles(@RequestPart List<MultipartFile> files);

    /**
     * Method for uploading a file.
     *
     * @param file file to save.
     * @return url of the saved file.
     */
    @PostMapping(path = "/files/single", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart MultipartFile file);

    /**
     * Method for deleting files.
     *
     * @param paths urls of files to delete.
     */
    @DeleteMapping("/files")
    void deleteAllFiles(@RequestBody List<String> paths);

    /**
     * Method returns all achievements.
     *
     * @return list of {@link AchievementVO}
     */
    @GetMapping("/achievements/all")
    List<AchievementVO> findAllAchievements();

    /**
     * Method returns all user achievements by user id.
     *
     * @param userId id of the user
     * @return list of {@link UserAchievementVO}
     */
    @GetMapping("/achievements/user-achievements/{userId}")
    List<UserAchievementVO> findAllUserAchievementsByUserId(@PathVariable Long userId);

    /**
     * Method returns all user actions by user id.
     *
     * @param userId id of the user
     * @return list of {@link UserActionVO}
     */
    @GetMapping("/achievements/user-actions/{userId}")
    List<UserActionVO> findAllUserActionsByUserId(@PathVariable Long userId);

    /**
     * Method for finding Language by id.
     *
     * @return {@link LanguageVO}
     */
    @GetMapping("/languages/{id}")
    LanguageVO findLanguageById(@PathVariable Long id);

    /**
     * Check whether Language exists by id.
     *
     * @return boolean of whether language exists by that id
     */
    @GetMapping("/languages/{id}/exists")
    Boolean languageExistsById(@PathVariable Long id);

    /**
     * Method to find {@link UserCityDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserCityDto}.
     */
    @GetMapping("/users/{id}/cities")
    UserCityDto findAllUsersCities(@PathVariable(name = "id") Long userId);

    /**
     * Method to find {@link UserLocationDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}.
     */
    @GetMapping("/users/{id}/location")
    UserLocationDto findUserLocationByUserId(@PathVariable(name = "id") Long userId);

    /**
     * Method to update user location by user id.
     *
     * @param userId                id of the user
     * @param userProfileDtoRequest contains location data
     */
    @PatchMapping("/users/{id}/location")
    void setLocationForUser(
        @PathVariable(name = "id") Long userId,
        @RequestBody UserProfileDtoRequest userProfileDtoRequest);

    /**
     * Get all user's friends ids by user id.
     *
     * @param userId id of the user.
     * @return list of friends ids.
     */
    @GetMapping("/users/{id}/all-friends")
    List<Long> getAllUserFriendsIds(@PathVariable("id") Long userId);

    /**
     * Get all user friends ids as a page.
     *
     * @param userId   id of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    @GetMapping("/users/{id}/friends")
    Page<Long> getAllUserFriendsIds(@PathVariable("id") Long userId, @SpringQueryMap Pageable pageable);

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of friends ids
     */
    @GetMapping("/users/{id}/top-friends")
    List<Long> getSixFriendsIdsWithTheHighestRating(@PathVariable("id") Long userId);

    /**
     * Increase user rating by amount specified in {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    @PatchMapping("/users/rating")
    void updateUserRating(@RequestBody UserAddRatingDto userAddRatingDto);

    /**
     * Synchronize GreenCityUser and GreenCity user entity via update.
     *
     * @param updateUserDto {@link UpdateUserDto} contains data for PATCH-update.
     */
    @PatchMapping("/users/update")
    boolean updateUser(@RequestBody UpdateUserDto updateUserDto);
}
