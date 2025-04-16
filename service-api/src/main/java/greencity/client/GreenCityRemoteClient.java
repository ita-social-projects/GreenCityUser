package greencity.client;

import greencity.client.config.GreenCityRemoteClientFallbackFactory;
import greencity.client.config.GreenCityRemoteClientInterceptor;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "greencity-remote-client",
        url = "${greencity.server.address}",
        configuration = GreenCityRemoteClientInterceptor.class,
        fallbackFactory = GreenCityRemoteClientFallbackFactory.class)
public interface GreenCityRemoteClient {

    /**
     * Method for uploading a files.
     *
     * @param files files to save.
     * @return urls of the saved files.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Optional<List<String>> uploadFile(@RequestPart @NonNull List<MultipartFile> files);

}
