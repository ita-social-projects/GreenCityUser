package greencity.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import greencity.properties.SecurityProperties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoadingCache<String, Integer> attemptsByWrongPasswordCache;
    private SecurityProperties securityProperties;

    public LoginAttemptServiceImpl(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.attemptsByWrongPasswordCache = CacheBuilder.newBuilder()
            .expireAfterWrite(securityProperties.getBruteForceBlockTime(), TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Integer load(final String key) {
                    return 0;
                }
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loginFailedByWrongPassword(String email) {
        attemptsByWrongPasswordCache.asMap().merge(email, 1, Integer::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockedByWrongPassword(String email) {
        try {
            return attemptsByWrongPasswordCache.get(email) >= securityProperties.getBruteForceMaxAttempts();
        } catch (final ExecutionException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEmailFromCache(String email) {
        attemptsByWrongPasswordCache.invalidate(email);
    }
}
