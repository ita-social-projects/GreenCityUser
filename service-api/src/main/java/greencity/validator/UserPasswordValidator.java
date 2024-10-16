package greencity.validator;

import greencity.annotations.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

public class UserPasswordValidator implements ConstraintValidator<PasswordValidation, String> {
    private final PasswordValidator validator;

    /**
     * Default constructor that init PasswordValidator.
     */
    public UserPasswordValidator() {
        this.validator = new PasswordValidator(
            new LengthRule(8, 20),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new WhitespaceRule());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        RuleResult result = validator.validate(new PasswordData(value));
        if (result.isValid()) {
            return true;
        } else {
            String message = validator.getMessages(result).stream().reduce((x, y) -> x + " " + y)
                .orElse("Invalid password format!");
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
    }
}
