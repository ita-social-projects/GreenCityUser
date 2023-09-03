package greencity.security.filters;

import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserOnlineStatusUpdateFilterTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain chain;
    @Mock
    UserService userService;

    @InjectMocks
    private UserOnlineStatusUpdateFilter userOnlineStatusUpdateFilter;

    @Test
    @SneakyThrows
    void doFilterInternalTest() {
        String email = "test@gmail.com";

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userOnlineStatusUpdateFilter.doFilterInternal(request, response, chain);

        verify(userService).updateUserLastActivityTimeByEmail(eq(email), any());
        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    void doFilterInternalTestWhenUserIsNotAuthorized() {
        userOnlineStatusUpdateFilter.doFilterInternal(request, response, chain);

        verify(userService, never()).updateUserLastActivityTimeByEmail(anyString(), any());
        verify(chain).doFilter(request, response);
    }
}
