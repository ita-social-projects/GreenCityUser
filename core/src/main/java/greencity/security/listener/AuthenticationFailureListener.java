package greencity.security.listener;

import greencity.security.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 * Listens for {@link AuthenticationFailureBadCredentialsEvent} events and
 * delegates work of tracking of login attempts to {@link LoginAttemptService}.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final HttpServletRequest request;
    private final LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(@NotNull AuthenticationFailureBadCredentialsEvent event) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
}
