package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageVO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceImplTest {

    @Mock
    LanguageRepo languageRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    LanguageServiceImpl languageService;

    Language language1;
    Language language2;
    LanguageVO languageVO1;
    LanguageVO languageVO2;

    @BeforeEach
    void setup() {
        language1 = new Language();
        language1.setId(1L);
        language1.setCode("en");

        language2 = new Language();
        language2.setId(2L);
        language2.setCode("fr");

        languageVO1 = new LanguageVO();
        languageVO1.setId(1L);
        languageVO1.setCode("en");

        languageVO2 = new LanguageVO();
        languageVO2.setId(2L);
        languageVO2.setCode("fr");
    }

    @Test
    void getAllLanguagesTest() {
        List<Language> languages = Arrays.asList(language1, language2);
        when(languageRepo.findAll()).thenReturn(languages);
        when(modelMapper.map(language1, LanguageVO.class)).thenReturn(languageVO1);
        when(modelMapper.map(language2, LanguageVO.class)).thenReturn(languageVO2);

        List<LanguageVO> result = languageService.getAllLanguages();

        assertEquals(2, result.size());
        assertEquals(languageVO1, result.get(0));
        assertEquals(languageVO2, result.get(1));
        verify(languageRepo, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Language.class), eq(LanguageVO.class));
    }

    @Test
    void findLanguageByCodeTest() {
        String code = "en";
        when(languageRepo.findByCode(code)).thenReturn(Optional.of(language1));
        when(modelMapper.map(language1, LanguageVO.class)).thenReturn(languageVO1);

        LanguageVO result = languageService.findLanguageByCode(code);

        assertNotNull(result);
        assertEquals(languageVO1, result);
        verify(languageRepo, times(1)).findByCode(code);
        verify(modelMapper, times(1)).map(language1, LanguageVO.class);
    }

    @Test
    void findLanguageByCodeTestThrowsLanguageNotException() {
        String code = "invalid";
        when(languageRepo.findByCode(code)).thenReturn(Optional.empty());

        LanguageNotFoundException exception = assertThrows(
            LanguageNotFoundException.class,
            () -> languageService.findLanguageByCode(code));

        assertTrue(exception.getMessage().contains(ErrorMessage.LANGUAGE_NOT_FOUND_BY_CODE + code));
        verify(languageRepo, times(1)).findByCode(code);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findLanguageByIdTest() {
        Long id = 1L;
        when(languageRepo.findById(id)).thenReturn(Optional.of(language1));
        when(modelMapper.map(language1, LanguageVO.class)).thenReturn(languageVO1);

        LanguageVO result = languageService.findLanguageById(id);

        assertNotNull(result);
        assertEquals(languageVO1, result);
        verify(languageRepo, times(1)).findById(id);
        verify(modelMapper, times(1)).map(language1, LanguageVO.class);
    }

    @Test
    void findLanguageByIdTestThrowsLanguageNotFoundException() {
        Long id = 999L;
        when(languageRepo.findById(id)).thenReturn(Optional.empty());

        LanguageNotFoundException exception = assertThrows(
            LanguageNotFoundException.class,
            () -> languageService.findLanguageById(id));

        assertTrue(exception.getMessage().contains(ErrorMessage.LANGUAGE_NOT_FOUND_BY_ID + id));
        verify(languageRepo, times(1)).findById(id);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findAllLanguageCodesTest() {
        List<String> expectedCodes = Arrays.asList("en", "fr", "de");
        when(languageRepo.findAllLanguageCodes()).thenReturn(expectedCodes);

        List<String> result = languageService.findAllLanguageCodes();

        assertEquals(expectedCodes, result);
        verify(languageRepo, times(1)).findAllLanguageCodes();
    }
}
