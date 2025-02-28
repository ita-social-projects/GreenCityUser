package greencity.dto.security;

import java.util.List;

public record CloudFlareResponse(
    boolean success,
    List<String> errorCodes,
    String challenge_ts,
    String hostname) {
}
