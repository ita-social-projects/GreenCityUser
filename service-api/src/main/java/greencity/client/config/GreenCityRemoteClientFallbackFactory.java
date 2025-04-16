package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.GreenCityRemoteClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GreenCityRemoteClientFallbackFactory implements FallbackFactory<GreenCityRemoteClient> {
    @Override
    public GreenCityRemoteClient create(Throwable throwable) {
        return new GreenCityRemoteClient() {
            @Override
            public Optional<List<String>> uploadAllFiles(List<MultipartFile> files) {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<String> uploadFile(MultipartFile file) {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public void deleteAllFiles(List<String> paths) {
                //TODO: log
            }
        };
    }
}
