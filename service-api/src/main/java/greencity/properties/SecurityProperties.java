package greencity.properties;

import greencity.constant.ErrorMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Сlass for retrieving configuration values from the runtime environment. Used
 * to access dynamic properties. Provides a flexible alternative to @Value,
 * always getting the latest values without having to restart the application or
 * use /actuator/refresh.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getAccessTokenExpiration();
        getRefreshTokenExpiration();
        getAccessTokenKey();
        getVerifyEmailExpiration();
        getBruteForceMaxAttempts();
        getBruteForceBlockTime();
        getTesterSignInToken();
        log.info("All security properties validated successfully.");
    }

    public Integer getAccessTokenExpiration() {
        Integer accessTokenExpiration =
            environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class);
        if (accessTokenExpiration == null) {
            log.error(ErrorMessage.ACCESS_TOKEN_EXPIRATION_NOT_SET);
            throw new IllegalStateException(ErrorMessage.ACCESS_TOKEN_EXPIRATION_NOT_SET);
        }
        return accessTokenExpiration;
    }

    public Integer getRefreshTokenExpiration() {
        Integer refreshTokenExpiration =
            environment.getProperty("security.jwt.refresh-token.expiration-minutes", Integer.class);
        if (refreshTokenExpiration == null) {
            log.error(ErrorMessage.REFRESH_TOKEN_EXPIRATION_NOT_SET);
            throw new IllegalStateException(ErrorMessage.REFRESH_TOKEN_EXPIRATION_NOT_SET);
        }
        return refreshTokenExpiration;
    }

    public String getAccessTokenKey() {
        String accessTokenKey = environment.getProperty("security.jwt.secret-key");
        if (!StringUtils.hasText(accessTokenKey)) {
            log.error(ErrorMessage.ACCESS_TOKEN_NOT_SET);
            throw new IllegalStateException(ErrorMessage.ACCESS_TOKEN_NOT_SET);
        }
        return accessTokenKey;
    }

    public Integer getVerifyEmailExpiration() {
        Integer verifyEmailExpiration =
            environment.getProperty("security.jwt.verify-email.expiration-hours", Integer.class);
        if (verifyEmailExpiration == null) {
            log.error(ErrorMessage.VERIFY_EMAIL_EXPIRATION_NOT_SET);
            throw new IllegalStateException(ErrorMessage.VERIFY_EMAIL_EXPIRATION_NOT_SET);
        }
        return verifyEmailExpiration;
    }

    public int getBruteForceMaxAttempts() {
        Integer bruteForceMaxAttempts = environment.getProperty("security.brute-force.max-attempts", Integer.class);
        if (bruteForceMaxAttempts == null) {
            log.error(ErrorMessage.BRUTEFORCE_MAX_ATTEMPTS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.BRUTEFORCE_MAX_ATTEMPTS_NOT_SET);
        }
        return bruteForceMaxAttempts;
    }

    public long getBruteForceBlockTime() {
        Long bruteForceBlockTime = environment.getProperty("security.brute-force.block-time-minutes", Long.class);
        if (bruteForceBlockTime == null) {
            log.error(ErrorMessage.BRUTEFORCE_BLOCK_TIME_NOT_SET);
            throw new IllegalStateException(ErrorMessage.BRUTEFORCE_BLOCK_TIME_NOT_SET);
        }
        return bruteForceBlockTime;
    }

    public String getTesterSignInToken() {
        String testerSignInToken = environment.getProperty("testers.sign-in-token");
        if (!StringUtils.hasText(testerSignInToken)) {
            log.error(ErrorMessage.TESTER_SIGN_IN_TOKEN_NOT_SET);
            throw new IllegalStateException(ErrorMessage.TESTER_SIGN_IN_TOKEN_NOT_SET);
        }
        return testerSignInToken;
    }
}