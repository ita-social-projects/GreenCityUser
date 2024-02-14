package greencity.constant;

public final class ValidationConstants {
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";
    public static final String USERNAME_MESSAGE =
        """
            The name ${validatedValue} cannot be empty, \
            starts with a number or not a capital letter, \
            ends with dot, \
            contain 2 consecutive dots or dashes and symbols like @#$. \
            Use English or Ukrainian letters, \
            no longer than 30 symbols, \
            the name ${validatedValue} could contain numbers, symbols '’, \
            dot in the middle of the name, dash and whitespaces.\
            """;

    public static final String USER_CREATED = "{greenCity.validation.user.created}";
    public static final int MAX_AMOUNT_OF_SOCIAL_NETWORK_LINKS = 5;
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final String CATEGORY_NAME_BAD_FORMED = "{greenCity.validation.bad.formed.category.name}";
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final String USERNAME_REGEXP =
        """
            ^(?!.*\\.\\.)(?!.*\\.$)(?!.*\\-\\-)\
            (?=[ЄІЇҐЁА-ЯA-Z])\
            [ЄІЇҐЁєіїґёА-Яа-яA-Za-z0-9\\s-'’.\\"]\
            {1,30}\
            (?<![ЭэЁёъЪЫы])$\
            """;

    private ValidationConstants() {
    }
}
