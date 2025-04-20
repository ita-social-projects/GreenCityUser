package greencity.client;

import greencity.client.config.GreenCityRemoteClientFallbackFactory;
import greencity.client.config.GreenCityRemoteClientInterceptor;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.useraction.UserActionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
     *
     * @return list of {@link UserAchievementVO}
     */
    @GetMapping("/achievements/user-achievements/{userId}")
    List<UserAchievementVO> findAllUserAchievementsByUserId(@PathVariable Long userId);

    /**
     * Method returns all user actions by user id.
     *
     * @param userId id of the user
     *
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
}
