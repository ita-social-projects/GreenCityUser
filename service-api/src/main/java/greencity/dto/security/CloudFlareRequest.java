package greencity.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CloudFlareRequest(
    @NotBlank String secret,
    @NotBlank String response,
    @JsonProperty("remoteip") String remoteIp) {
}
