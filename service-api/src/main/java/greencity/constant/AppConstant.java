package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {
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
        """
            (?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\
            \\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*")@(?:(?:[a-zA-Z0-9](?:\
            [a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|\
            [01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\
            \\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])\
            """;
    public static final Double DEFAULT_RATING = 0.0;
    public static final String USERNAME = "name";
    public static final String FACEBOOK_OBJECT_ID = "me";
    public static final String FILES = "files";
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String PASSWORD = "password";
    public static final String USER_STATUS = "user_status";
    public static final String GOOGLE_API = "Google API";
    public static final String XFF_HEADER = "X-Forwarded-For";
}
