package greencity.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

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
     * This method validate language.
     *
     * @param emailAddress to be validated
     * @return true if everything is ok
     */
    public static boolean isValid(String emailAddress) {
        Validate.notBlank(emailAddress, "Email address couldn't be blank");
        Pattern regexPattern = Pattern.compile("^[(A-z-0-9+_.)]+@[(A-z)]+\\.[(A-z)]{2,3}$");
        Matcher regMatcher = regexPattern.matcher(emailAddress);
        return regMatcher.matches();
    }
}
