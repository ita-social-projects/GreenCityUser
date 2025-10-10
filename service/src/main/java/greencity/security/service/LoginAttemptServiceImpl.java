package greencity.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoadingCache<String, Integer> attemptsByWrongPasswordCache;
    @Value("${security.brute-force.max-attempts}")
    private int maxAttempt;

    public LoginAttemptServiceImpl(@Value("${security.brute-force.block-time-minutes}") int blockTimeInMinutes) {
        this.attemptsByWrongPasswordCache = CacheBuilder.newBuilder()
            .expireAfterWrite(blockTimeInMinutes, TimeUnit.MINUTES)
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
            return attemptsByWrongPasswordCache.get(email) >= maxAttempt;
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
