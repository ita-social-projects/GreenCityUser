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
    /**
     * This method validate emailAddress.
     *
     * @param emailAddress to be validated
     */
    public static void validate(String emailAddress) {
        if (emailAddress == null) {
            throw new WrongEmailException("Email address cannot be null");
        }
        Pattern p = Pattern.compile(AppConstant.VALIDATION_EMAIL);
        Matcher m = p.matcher(emailAddress);
        if (!m.matches()) {
            throw new WrongEmailException("Invalid email address " + emailAddress);
        }
    }
}
