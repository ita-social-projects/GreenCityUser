package greencity.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateConstantsTest {

    @Test
    void testGetResultByLanguageCode_WithUACode_ReturnsSuccessUA() {
        String languageCode = "ua";
        String result = UpdateConstants.getResultByLanguageCode(languageCode);
        assertEquals(UpdateConstants.SUCCESS_UA, result);
    }

    @Test
    void testGetResultByLanguageCode_WithENCode_ReturnsSuccessEN() {
        String languageCode = "en";
        String result = UpdateConstants.getResultByLanguageCode(languageCode);
        assertEquals(UpdateConstants.SUCCESS_EN, result);
    }

    @Test
    void testGetResultByLanguageCode_WithInvalidCode_ReturnsSuccessEN() {
        String languageCode = "invalid";
        String result = UpdateConstants.getResultByLanguageCode(languageCode);
        assertEquals(UpdateConstants.SUCCESS_EN, result);
    }
}