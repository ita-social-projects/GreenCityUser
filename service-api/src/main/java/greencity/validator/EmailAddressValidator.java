package greencity.validator;

import greencity.constant.AppConstant;
import greencity.exception.exceptions.WrongEmailException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class should validate email.
 *
 * @author Volodymyr Mladonov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAddressValidator {
    public static final Pattern REGEX_PATTERN = Pattern.compile(AppConstant.VALIDATION_EMAIL_REGEXP);

    /**
     * This method validate emailAddress.
     *
     * @param emailAddress to be validated
     */
    public static void validate(String emailAddress) {
        if (emailAddress == null) {
            throw new WrongEmailException("Email address cannot be null");
        }
        Matcher m = REGEX_PATTERN.matcher(emailAddress);
        if (!m.matches()) {
            throw new WrongEmailException("Invalid email address " + emailAddress);
        }
    }
}
