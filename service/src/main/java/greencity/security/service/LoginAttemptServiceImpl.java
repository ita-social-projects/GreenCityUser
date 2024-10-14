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
    private final LoadingCache<String, Integer> attemptsByCaptchaCache;
    private final LoadingCache<String, Integer> attemptsByWrongPasswordCache;
    @Value("${bruteForceSettings.maxAttempts}")
    private int maxAttempt;

    public LoginAttemptServiceImpl(@Value("${bruteForceSettings.blockTimeInHours}") int blockTimeInHours,
        @Value("${bruteForceSettings.blockTimeInMinutes}") int blockTimeInMinutes) {
        this.attemptsByCaptchaCache = CacheBuilder.newBuilder()
            .expireAfterWrite(blockTimeInHours, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public Integer load(final String key) {
                    return 0;
                }
            });
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
    public void loginFailedByCaptcha(final String key) {
        attemptsByCaptchaCache.asMap().merge(key, 1, Integer::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockedByCaptcha(String email) {
        try {
            return attemptsByCaptchaCache.get(email) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
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
}
