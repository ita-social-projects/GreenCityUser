package greencity.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.service.LanguageService;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private ConstraintValidatorContext context;
    @InjectMocks
    private LanguageValidator validator;

    @Test
    void initializeSuccessTest() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en", "uk"));
        validator.initialize(null);
        assertTrue(validator.isValid(Locale.ENGLISH, context));
        assertTrue(validator.isValid(Locale.of("uk"), context));
        assertFalse(validator.isValid(Locale.of("de"), context));
    }

    @Test
    void initializeUnSuccessTest() {
        when(languageService.findAllLanguageCodes()).thenThrow(new RuntimeException("mock error"));
        validator.initialize(null);
        assertTrue(validator.isValid(Locale.ENGLISH, context));
        assertTrue(validator.isValid(Locale.of("uk"), context));
        assertFalse(validator.isValid(Locale.of("de"), context));
    }
}