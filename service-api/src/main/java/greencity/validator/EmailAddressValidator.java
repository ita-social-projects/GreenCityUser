package greencity.validator;

import greencity.constant.AppConstant;
import greencity.exception.exceptions.WrongEmailException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

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
     */
    public static void validate(String emailAddress) {
        Validate.notBlank(emailAddress, "Email address couldn't be blank");
        if (!emailAddress.matches(AppConstant.VALIDATION_EMAIL)) {
            throw new WrongEmailException("Invalid email address " + emailAddress);
        }
    }
}
