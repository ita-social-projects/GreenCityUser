package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {
    public static final String UKRAINE_TIMEZONE = "Europe/Kiev";
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String REGISTRATION_EMAIL_FIELD_NAME = "email";
    public static final String GOOGLE_PICTURE = "picture";
    public static final String ADMIN = "ADMIN";
    public static final String MODERATOR = "MODERATOR";
    public static final String USER = "USER";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String UBS_EMPLOYEE = "UBS_EMPLOYEE";
    public static final String ROLE = "role";
    public static final String AUTHORIZATION = "Authorization";
    public static final String VALIDATION_EMAIL_REGEXP =
        "^(?=.{3,72}$)"
            + "([a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+"
            + "(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+)*)"
            + "@"
            + "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+"
            + "[a-zA-Z]{2,63}|"
            + "\\[(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)"
            + "(?:\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)){3}\\])$";
    public static final Double DEFAULT_RATING = 0.0;
    public static final String USERNAME = "name";
    public static final String FACEBOOK_OBJECT_ID = "me";
    public static final String FILES = "files";
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String PASSWORD = "password";
    public static final String USER_STATUS = "user_status";
    public static final String GOOGLE_API = "Google API";
}
