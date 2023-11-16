package greencity.validator;

import org.apache.commons.lang3.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class should validate email.
 *
 * @author Volodymyr Mladonov
 */
public class EmailAddressValidator {
    /**
     * This method validate language.
     *
     * @param emailAddress to be validated
     * @return true if everything is ok
     */
    public static boolean isValid(String emailAddress) {
        Validate.notBlank(emailAddress, "Email address couldn't be blank");
        Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-_+.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher = regexPattern.matcher(emailAddress);
        return regMatcher.matches();
    }
}
