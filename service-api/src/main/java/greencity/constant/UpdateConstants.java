package greencity.constant;

public final class UpdateConstants {
    public static final String SUCCESS_UA = "Користувача успішно оновлено.";
    public static final String SUCCESS_EN = "User successfully updated.";
    public static final String SUCCESS_RU = "Пользователь успешно обновлен.";

    private UpdateConstants() {
    }

    /**
     * Method return user message depends on users language.
     *
     * @author Volodia Lesko
     */
    public static String getResultByLanguageCode(String code) {
        if (code.equals("ua")) {
            return SUCCESS_UA;
        } else if (code.equals("ru")) {
            return SUCCESS_RU;
        } else {
            return SUCCESS_EN;
        }
    }
}
