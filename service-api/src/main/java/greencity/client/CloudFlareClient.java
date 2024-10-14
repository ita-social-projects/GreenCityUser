package greencity.client;

import greencity.client.config.CloudFlareClientFallbackFactory;
import greencity.dto.security.CloudFlareResponse;
import greencity.dto.security.CloudFlareRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "cloudflare",
    url = "https://challenges.cloudflare.com/turnstile/v0/siteverify",
    fallbackFactory = CloudFlareClientFallbackFactory.class)
public interface CloudFlareClient {
    @PostMapping(consumes = "application/json", produces = "application/json")
    CloudFlareResponse getCloudFlareResponse(@Valid @RequestBody CloudFlareRequest request);
}
