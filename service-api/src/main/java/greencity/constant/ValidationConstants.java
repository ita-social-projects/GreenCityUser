package greencity.constant;

public final class ValidationConstants {
    public static final int USERNAME_MIN_LENGTH = 0;
    public static final int USERNAME_MAX_LENGTH = 29;
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";
    public static final String INVALID_USERNAME = "{greenCity.validation.invalid.username}";
    public static final String USER_CREATED = "{greenCity.validation.user.created}";
    public static final int MAX_AMOUNT_OF_SOCIAL_NETWORK_LINKS = 5;
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final String CATEGORY_NAME_BAD_FORMED = "{greenCity.validation.bad.formed.category.name}";
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final String USERNAME_REGEXP =
        "^(?!.*\\.\\.)(?!.*\\.$)(?!.*\\-\\-)[ЁІЇҐЄА-Я-A-Z][ЁёІіЇїҐґЄєА-Яа-яA-Za-z0-9\\s-ʼ'`ʹ,.\\\"]{0,29}$";

    private ValidationConstants() {
    }
}
