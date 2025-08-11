package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String EMAIL_REGEXP =
        "^(?=.{3,72}$)"
            + "([a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+"
            + "(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+)*)"
            + "@"
            + "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+"
            + "[a-zA-Z]{2,63}|"
            + "\\[(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)"
            + "(?:\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)){3}\\])$";
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";

    public static final String USER_CREATED = "{greenCity.validation.user.created}";
}
