package greencity.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoadingCache<String, Integer> attemptsCache;
    private final HttpServletRequest request;
    @Value("${bruteForceSettings.maxAttempts}")
    private int maxAttempt;

    public LoginAttemptServiceImpl(HttpServletRequest request,
        @Value("${bruteForceSettings.blockTimeInHours}") long blockTimeInHours) {
        super();
        this.request = request;
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(blockTimeInHours, TimeUnit.HOURS)
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
    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
