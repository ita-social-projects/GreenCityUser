package greencity.security.service;

import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginAttemptServiceImplTest {
    @Mock
    private LoadingCache<String, Integer> attemptsCache;
    @Mock
    private HttpServletRequest request;
    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        loginAttemptService = new LoginAttemptServiceImpl(request, 1);
        Field attemptsCacheField = LoginAttemptServiceImpl.class.getDeclaredField("attemptsCache");
        attemptsCacheField.setAccessible(true);
        attemptsCacheField.set(loginAttemptService, attemptsCache);
        Field maxAttemptField = LoginAttemptServiceImpl.class.getDeclaredField("maxAttempt");
        maxAttemptField.setAccessible(true);
        maxAttemptField.set(loginAttemptService, 5);
    }

    @Test
    void testLoginFailed() throws ExecutionException {
        when(attemptsCache.get(anyString())).thenReturn(0);

        loginAttemptService.loginFailed("testKey");

        verify(attemptsCache).put("testKey", 1);
    }

    @Test
    void testIsBlockedNotBlocked() throws ExecutionException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(attemptsCache.get(anyString())).thenReturn(1);

        assertFalse(loginAttemptService.isBlocked());
    }

    @Test
    void testIsBlockedAreBlocked() throws ExecutionException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(attemptsCache.get(anyString())).thenReturn(5);

        assertTrue(loginAttemptService.isBlocked());
    }
}
