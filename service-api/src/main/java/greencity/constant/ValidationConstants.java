package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";
    public static final String USERNAME_MESSAGE = """
        Name must start with a letter, \
        cannot end with dot \
        or contain 2 consecutive dots, dashes and special symbols. \
        Use English or Ukrainian letters, \
        no longer than 30 symbols.\
        """;

    public static final String USER_CREATED = "{greenCity.validation.user.created}";
    public static final int MAX_AMOUNT_OF_SOCIAL_NETWORK_LINKS = 5;
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final String CATEGORY_NAME_BAD_FORMED = "{greenCity.validation.bad.formed.category.name}";
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final String USERNAME_REGEXP = """
        ^(?!.*\\.\\.)(?!.*\\.$)(?!.*\\-\\-)\
        (?=[ЄІЇҐЁєіїґёА-Яа-яA-Za-z])\
        [ЄІЇҐЁєіїґёА-Яа-яA-Za-z0-9\\s\\-'\\"’.ʼ]\
        {1,30}\
        (?<![ЭэЁёъЪЫы])$\
        """;
}
