package greencity.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import greencity.constant.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoadingCache<String, Integer> attemptsCache;
    private final HttpServletRequest request;
    @Value("${bruteForceSettings.maxAttempts}")
    private int maxAttempt;

    public LoginAttemptServiceImpl(HttpServletRequest request,
        @Value("${bruteForceSettings.blockTimeInHours}") long blockTimeInHours) {
        this.request = request;
        this.attemptsCache = CacheBuilder.newBuilder()
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
    @Override
    public void loginFailed(final String key) {
        attemptsCache.asMap().merge(key, 1, Integer::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader(AppConstant.XFF_HEADER);
        if (StringUtils.isNotEmpty(xfHeader)) {
            String[] xfHeaderParts = xfHeader.split(",");
            if (xfHeaderParts.length > 0) {
                return xfHeaderParts[0];
            }
            log.warn("Invalid xfHeader value: {}", xfHeader);
        }
        return request.getRemoteAddr();
    }
}
