package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.GreenCityRemoteClient;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserCityDto;
import greencity.dto.useraction.UserActionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Component
@Slf4j
public class GreenCityRemoteClientFallbackFactory implements FallbackFactory<GreenCityRemoteClient> {
    @Override
    public GreenCityRemoteClient create(Throwable throwable) {
        return new GreenCityRemoteClient() {
            @Override
            public List<String> uploadAllFiles(List<MultipartFile> files) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public String uploadFile(MultipartFile file) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public void deleteAllFiles(List<String> paths) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<AchievementVO> findAllAchievements() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<UserAchievementVO> findAllUserAchievementsByUserId(Long userId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<UserActionVO> findAllUserActionsByUserId(Long userId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public LanguageVO findLanguageById(Long languageId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public Boolean languageExistsById(Long languageId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public UserCityDto findAllUsersCities(Long userId) {
                throw new RuntimeException("not implemented");
            }
        };
    }
}
