package greencity.exception.exceptions;

import lombok.experimental.StandardException;
import org.springframework.security.core.AuthenticationException;

/**
 * Exception that we get when user trying to sign-in to account that is
 * deactivated.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@StandardException
public class UserDeactivatedException extends AuthenticationException {
}
