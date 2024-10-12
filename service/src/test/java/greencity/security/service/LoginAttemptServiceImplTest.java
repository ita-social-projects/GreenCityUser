package greencity.security.service;

import com.google.common.cache.LoadingCache;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginAttemptServiceImplTest {
    @Mock
    private LoadingCache<String, Integer> attemptsByCaptchaCache;
    @Mock
    private LoadingCache<String, Integer> attemptsByWrongPasswordCache;
    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        ConcurrentMap<String, Integer> mockMap = Mockito.mock(ConcurrentMap.class);
        when(attemptsByCaptchaCache.asMap()).thenReturn(mockMap);
        when(attemptsByWrongPasswordCache.asMap()).thenReturn(mockMap);

        loginAttemptService = new LoginAttemptServiceImpl(1, 15);

        Field attemptsByCaptchaCache = LoginAttemptServiceImpl.class
            .getDeclaredField("attemptsByCaptchaCache");
        attemptsByCaptchaCache.setAccessible(true);
        attemptsByCaptchaCache.set(this.loginAttemptService, this.attemptsByCaptchaCache);

        Field attemptsByWrongPasswordCache = LoginAttemptServiceImpl.class
            .getDeclaredField("attemptsByWrongPasswordCache");
        attemptsByWrongPasswordCache.setAccessible(true);
        attemptsByWrongPasswordCache.set(this.loginAttemptService, this.attemptsByWrongPasswordCache);

        Field maxAttemptField = LoginAttemptServiceImpl.class.getDeclaredField("maxAttempt");
        maxAttemptField.setAccessible(true);
        maxAttemptField.set(this.loginAttemptService, 5);
    }

    @Test
    void testLoginFailedByCaptcha() throws ExecutionException {
        when(attemptsByCaptchaCache.get(anyString())).thenReturn(0);

        loginAttemptService.loginFailedByCaptcha("testKey");

        ArgumentCaptor<BiFunction<Integer, Integer, Integer>> captor = ArgumentCaptor.forClass(BiFunction.class);

        verify(attemptsByCaptchaCache.asMap(), Mockito.times(1)).merge(eq("testKey"), eq(1), captor.capture());

        BiFunction<Integer, Integer, Integer> capturedFunction = captor.getValue();
        Integer result = capturedFunction.apply(0, 1);
        assertEquals(1, result);
    }

    @Test
    void testIsBlockedNotBlockedByCaptcha() throws ExecutionException {
        when(attemptsByCaptchaCache.get(anyString())).thenReturn(1);

        assertFalse(loginAttemptService.isBlockedByCaptcha(anyString()));
    }

    @Test
    void testIsBlockedAreBlockedByCaptcha() throws ExecutionException {
        when(attemptsByCaptchaCache.get(anyString())).thenReturn(5);

        assertTrue(loginAttemptService.isBlockedByCaptcha(anyString()));
    }

    @Test
    void testIsBlockedByCaptchaExecutionException() throws ExecutionException {
        when(attemptsByCaptchaCache.get(anyString()))
            .thenThrow(new ExecutionException(new Throwable("Cache error")));

        assertFalse(loginAttemptService.isBlockedByCaptcha("test@test.com"));

        verify(attemptsByCaptchaCache).get("test@test.com");
    }
}
