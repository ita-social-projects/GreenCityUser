package greencity.security.listener;

import greencity.constant.AppConstant;
import greencity.security.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationFailureListenerTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationFailureListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testOnApplicationEventWithXForwardedForHeader() {
        String xfHeader = "192.168.1.1";
        when(request.getHeader(AppConstant.XFF_HEADER)).thenReturn(xfHeader);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("user@example.com", "password");
        AuthenticationFailureBadCredentialsEvent event =
            new AuthenticationFailureBadCredentialsEvent(authentication,
                new BadCredentialsException("Bad credentials"));

        listener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed("192.168.0.1");
    }

    @Test
    void testOnApplicationEventWithoutXForwardedForHeader() {
        when(request.getHeader(AppConstant.XFF_HEADER)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("user@example.com", "password");
        AuthenticationFailureBadCredentialsEvent event =
            new AuthenticationFailureBadCredentialsEvent(authentication,
                new BadCredentialsException("Bad credentials"));

        listener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed("192.168.0.1");
    }

    @Test
    void testOnApplicationEventWithMultipleIPsInXForwardedForHeader() {
        String xfHeader = "203.0.113.195, 198.51.100.101";
        when(request.getHeader(AppConstant.XFF_HEADER)).thenReturn(xfHeader);
        when(request.getRemoteAddr()).thenReturn("203.0.113.195");

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("user@example.com", "password");
        AuthenticationFailureBadCredentialsEvent event =
            new AuthenticationFailureBadCredentialsEvent(authentication,
                new BadCredentialsException("Bad credentials"));

        listener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed("203.0.113.195");
    }
}
