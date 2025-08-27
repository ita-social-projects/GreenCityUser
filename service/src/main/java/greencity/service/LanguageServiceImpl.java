package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.language.LanguageVO;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepo languageRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageVO> getAllLanguages() {
        return languageRepo.findAll().stream()
            .map(language -> modelMapper.map(language, LanguageVO.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageVO findLanguageByCode(String code) {
        return languageRepo.findByCode(code)
            .map(language -> modelMapper.map(language, LanguageVO.class))
            .orElseThrow(() -> new LanguageNotFoundException(ErrorMessage.LANGUAGE_NOT_FOUND_BY_CODE + code));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllLanguageCodes() {
        return languageRepo.findAllLanguageCodes();
    }
}
