package greencity.validator;

import greencity.annotations.PasswordValidation;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserPasswordValidator implements ConstraintValidator<PasswordValidation, String> {
    private PasswordValidator validator;

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
    public void initialize(PasswordValidation constraintAnnotation) {
        // Initializes the validator in preparation for #isValid calls
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
