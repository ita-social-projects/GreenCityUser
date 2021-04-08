package greencity.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LanguageMapperTest {

    @InjectMocks
    private LanguageMapper languageMapper;

    @Test
    void convert() {
        assertThrows(IllegalStateException.class, () -> languageMapper.convert("e"));
        assertEquals(1L, languageMapper.convert("ua"));
        assertEquals(2L, languageMapper.convert("en"));
        assertEquals(3L, languageMapper.convert("ru"));
    }
}