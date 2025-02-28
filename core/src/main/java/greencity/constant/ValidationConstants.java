package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";

    public static final String USER_CREATED = "{greenCity.validation.user.created}";
}
