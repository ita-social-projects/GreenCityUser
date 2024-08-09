package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateConstants {
    public static final String SUCCESS_UA = "Користувача успішно оновлено.";
    public static final String SUCCESS_EN = "User successfully updated.";

    /**
     * Method return user message depends on users language.
     *
     * @author Volodia Lesko
     */
    public static String getResultByLanguageCode(String code) {
        if (code.equals("ua")) {
            return SUCCESS_UA;
        }
        return SUCCESS_EN;
    }
}
