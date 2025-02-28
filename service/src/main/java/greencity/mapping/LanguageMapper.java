package greencity.mapping;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper extends AbstractConverter<String, Long> {
    @Override
    protected Long convert(String lang) {
        return switch (lang) {
            case "ua" -> 1L;
            case "en" -> 2L;
            default -> throw new IllegalStateException("Unexpected value: " + lang);
        };
    }
}
