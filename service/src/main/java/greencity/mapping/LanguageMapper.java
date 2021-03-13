package greencity.mapping;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper extends AbstractConverter<String, Long> {
    // comment
    @Override
    protected Long convert(String lang) {
        switch (lang) {
            case "ua":
                return 1L;
            case "ru":
                return 3L;
            case "en":
                return 2L;
            default:
                throw new IllegalStateException("Unexpected value: " + lang);
        }
    }
}
