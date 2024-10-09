package greencity.security.listener;

import greencity.constant.AppConstant;
import greencity.security.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 * Listens for {@link AuthenticationFailureBadCredentialsEvent} events and
 * delegates work of tracking of login attempts to {@link LoginAttemptService}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final HttpServletRequest request;
    private final LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String xfHeader = request.getHeader(AppConstant.XFF_HEADER);

        if (StringUtils.isEmpty(xfHeader) || !xfHeader.contains(request.getRemoteAddr())) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            String[] xfHeaderParts = xfHeader.split(",");

            if (xfHeaderParts.length > 0) {
                loginAttemptService.loginFailed(xfHeaderParts[0]);
            } else {
                log.warn("Invalid xfHeader value: {}", xfHeader);
            }
        }
    }
}
