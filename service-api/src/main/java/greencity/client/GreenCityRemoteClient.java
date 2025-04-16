package greencity.client;

import greencity.client.config.GreenCityRemoteClientFallbackFactory;
import greencity.client.config.GreenCityRemoteClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

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
    Optional<List<String>> uploadAllFiles(@RequestPart List<MultipartFile> files);

    /**
     * Method for uploading a file.
     *
     * @param file file to save.
     * @return url of the saved file.
     */
    @PostMapping(path = "/files/single", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    Optional<String> uploadFile(@RequestPart MultipartFile file);

    /**
     * Method for deleting files.
     *
     * @param paths urls of files to delete.
     */
    @DeleteMapping("/files")
    void deleteAllFiles(@RequestBody List<String> paths);
}
