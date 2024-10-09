package greencity.security.service;

import com.google.common.cache.LoadingCache;
import greencity.constant.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private LoadingCache<String, Integer> attemptsCache;
    @Mock
    private HttpServletRequest request;
    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        ConcurrentMap<String, Integer> mockMap = Mockito.mock(ConcurrentMap.class);
        when(attemptsCache.asMap()).thenReturn(mockMap);

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

        ArgumentCaptor<BiFunction<Integer, Integer, Integer>> captor = ArgumentCaptor.forClass(BiFunction.class);

        verify(attemptsCache.asMap(), Mockito.times(1)).merge(eq("testKey"), eq(1), captor.capture());

        BiFunction<Integer, Integer, Integer> capturedFunction = captor.getValue();
        Integer result = capturedFunction.apply(0, 1);
        assertEquals(1, result);
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

    @Test
    void testGetClientIPDirectly() throws Exception {
        when(request.getHeader(AppConstant.XFF_HEADER)).thenReturn("192.168.1.1, 10.0.0.1");

        Method method = LoginAttemptServiceImpl.class.getDeclaredMethod("getClientIP");
        method.setAccessible(true);
        String clientIP = (String) method.invoke(loginAttemptService);

        assertEquals("192.168.1.1", clientIP);
    }

    @Test
    void testGetClientIPFallbackToRemoteAddr() throws Exception {
        when(request.getHeader(AppConstant.XFF_HEADER)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Method method = LoginAttemptServiceImpl.class.getDeclaredMethod("getClientIP");
        method.setAccessible(true);
        String clientIP = (String) method.invoke(loginAttemptService);

        assertEquals("127.0.0.1", clientIP);
    }

    @Test
    void testIsBlockedExecutionException() throws ExecutionException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(attemptsCache.get(anyString())).thenThrow(new ExecutionException(new Throwable("Cache error")));

        assertFalse(loginAttemptService.isBlocked());

        verify(attemptsCache).get("127.0.0.1");
    }
}
