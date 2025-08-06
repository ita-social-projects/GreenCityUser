package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageVO;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {

    @Mock
    LanguageService languageService;

    @InjectMocks
    LanguageController languageController;

    MockMvc mockMvc;

    LanguageVO languageVO1;
    LanguageVO languageVO2;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(languageController)
            .setControllerAdvice(new CustomExceptionHandler(new DefaultErrorAttributes())) // Assuming you have a global
                                                                                           // exception handler
            .defaultRequest(MockMvcRequestBuilders.get("/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .build();

        languageVO1 = new LanguageVO();
        languageVO1.setId(1L);
        languageVO1.setCode("en");
        languageVO1.setName("English");

        languageVO2 = new LanguageVO();
        languageVO2.setId(2L);
        languageVO2.setCode("fr");
        languageVO2.setName("French");
    }

    @Test
    void getAllLanguagesTest() throws Exception {
        List<LanguageVO> languages = Arrays.asList(languageVO1, languageVO2);
        when(languageService.getAllLanguages()).thenReturn(languages);

        mockMvc.perform(get("/lang")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].code", is("en")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].code", is("fr")));

        verify(languageService, times(1)).getAllLanguages();
        verifyNoMoreInteractions(languageService);
    }

    @Test
    void findLanguageByCodeTest() throws Exception {
        String code = "en";
        when(languageService.findLanguageByCode(code)).thenReturn(languageVO1);

        mockMvc.perform(get("/lang/codes/{code}", code)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.code", is("en")))
            .andExpect(jsonPath("$.name", is("English")));

        verify(languageService, times(1)).findLanguageByCode(code);
        verifyNoMoreInteractions(languageService);
    }

    @Test
    void findLanguageByCodeTestReturns400WhenLanguageNotFoundException() throws Exception {
        String code = "invalid";
        when(languageService.findLanguageByCode(code))
            .thenThrow(new LanguageNotFoundException(ErrorMessage.LANGUAGE_NOT_FOUND_BY_CODE + code));

        mockMvc.perform(get("/lang/codes/{code}", code)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(languageService, times(1)).findLanguageByCode(code);
        verifyNoMoreInteractions(languageService);
    }

    @Test
    void findAllLanguageCodesTest() throws Exception {
        List<String> codes = Arrays.asList("en", "fr", "de");
        when(languageService.findAllLanguageCodes()).thenReturn(codes);

        mockMvc.perform(get("/lang/codes")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0]", is("en")))
            .andExpect(jsonPath("$[1]", is("fr")))
            .andExpect(jsonPath("$[2]", is("de")));

        verify(languageService, times(1)).findAllLanguageCodes();
        verifyNoMoreInteractions(languageService);
    }
}
