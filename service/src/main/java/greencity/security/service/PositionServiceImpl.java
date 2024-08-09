package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.position.PositionAuthoritiesDto;
import greencity.entity.Authority;
import greencity.entity.Position;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionServiceImpl implements PositionService {
    private final UserRepo userRepo;

    @Override
    public PositionAuthoritiesDto getPositionsAndRelatedAuthorities(String email) {
        var user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return PositionAuthoritiesDto.builder()
            .positionId(user.getPositions().stream().map(Position::getId).toList())
            .authorities(user.getAuthorities().stream()
                .map(Authority::getName)
                .toList())
            .build();
    }
}
