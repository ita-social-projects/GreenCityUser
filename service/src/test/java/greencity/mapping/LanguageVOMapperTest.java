package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LanguageVOMapperTest {

    @InjectMocks
    LanguageVOMapper languageVOMapper;

    @Test
    void convertTest() {
        Language languageToConvert = Language.builder()
                .id(2L)
                .code("code")
                .build();
        LanguageVO expectedResult = LanguageVO.builder()
                .id(languageToConvert.getId())
                .code(languageToConvert.getCode())
                .build();

        LanguageVO actualResult = languageVOMapper.convert(languageToConvert);

        assertEquals(expectedResult, actualResult);
    }

}
