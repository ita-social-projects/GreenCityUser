package greencity.service;

import greencity.dto.language.LanguageVO;
import java.util.List;

public interface LanguageService {
    /**
     * Method to get all languages as {@link LanguageVO}.
     *
     * @return {@link List} of {@link LanguageVO}
     */
    List<LanguageVO> getAllLanguages();

    /**
     * Find language {@link LanguageVO} by code.
     *
     * @param code language code
     * @return language {@link LanguageVO}
     */
    LanguageVO findLanguageByCode(String code);

    /**
     * Find language {@link LanguageVO} by id.
     *
     * @param id language id
     * @return language {@link LanguageVO}
     */
    LanguageVO findLanguageById(Long id);

    /**
     * Method to get all language codes.
     *
     * @return {@link List} of {@link String} language codes
     */
    List<String> findAllLanguageCodes();
}
