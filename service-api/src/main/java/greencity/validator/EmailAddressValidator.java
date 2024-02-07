package greencity.validator;

import greencity.constant.AppConstant;
import greencity.exception.exceptions.WrongEmailException;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
        if (emailAddress == null || !REGEX_PATTERN.matcher(emailAddress).matches()) {
            throw new WrongEmailException("Invalid email address: " + emailAddress);
        }
    }
}
