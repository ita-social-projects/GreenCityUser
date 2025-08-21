package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.authorities.AuthorityCategoryDto;
import greencity.dto.authorities.AuthorityDto;
import greencity.dto.position.PositionDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.Authority;
import greencity.entity.AuthorityCategory;
import greencity.entity.Position;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AuthorityCategoryRepo;
import greencity.repository.AuthorityRepo;
import greencity.repository.PositionRepo;
import greencity.repository.UserRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private final UserRepo userRepo;
    private final AuthorityCategoryRepo authorityCategoryRepo;
    private final AuthorityRepo authorityRepo;
    private final PositionRepo positionRepo;

    @Override
    public Set<String> getAllEmployeesAuthorities(String email) {
        User user =
            userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        return authorityRepo.getAuthoritiesByEmployeeId(user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEmployeesAuthorities(UserEmployeeAuthorityDto dto) {
        User employee = userRepo.findByEmail(dto.getEmployeeEmail())
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmployeeEmail()));
        if (!employee.getRole().equals(Role.ROLE_UBS_EMPLOYEE)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        List<Authority> authorities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getAuthorities())) {
            authorities = authorityRepo.findAuthoritiesByNames(dto.getAuthorities());
        }
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    @Override
    public void updateAuthoritiesToRelatedPositions(EmployeePositionsDto dto) {
        User employee = userRepo.findByEmail(dto.getEmail()).orElseThrow(
            () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + dto.getEmail()));

        List<Long> positionIds = dto.getPositions() == null
            ? List.of()
            : dto.getPositions().stream()
                .map(PositionDto::getId)
                .distinct()
                .toList();
        List<Position> positions = positionRepo.findAllById(positionIds);
        List<Authority> authorities = positionIds.isEmpty()
            ? List.of()
            : authorityRepo.findAllByPositionIdsIn(positionIds);

        employee.setPositions(positions);
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    @Override
    public List<AuthorityDto> getAuthoritiesByCategory(Long categoryId) {
        authorityCategoryRepo.findById(categoryId)
            .orElseThrow(
                () -> new NotFoundException(String.format(ErrorMessage.AUTHORITY_CATEGORY_NOT_FOUND, categoryId)));
        List<Authority> authorities = authorityRepo.findAllByCategoryId(categoryId);
        return authorities.stream()
            .map(this::toAuthorityDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityCategoryDto> getAllAuthorityCategories() {
        List<AuthorityCategory> categories = authorityCategoryRepo.findAll();
        return categories.stream()
            .map(this::toAuthorityCategoryDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityCategoryDto> getEmployeesAuthoritiesGroupedByCategories(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));

        List<Authority> authorities = user.getAuthorities();

        return authorities.stream()
            .collect(Collectors.groupingBy(Authority::getCategory))
            .entrySet()
            .stream()
            .map(entry -> AuthorityCategoryDto.builder()
                .id(entry.getKey().getId())
                .nameEn(entry.getKey().getNameEn())
                .nameUk(entry.getKey().getNameUk())
                .authorities(entry.getValue().stream().map(this::toAuthorityDto).toList())
                .build())
            .toList();
    }

    private AuthorityDto toAuthorityDto(Authority authority) {
        return AuthorityDto.builder()
            .name(authority.getName())
            .descriptionEn(authority.getDescriptionEn())
            .descriptionUk(authority.getDescriptionUk())
            .build();
    }

    private AuthorityCategoryDto toAuthorityCategoryDto(AuthorityCategory category) {
        List<AuthorityDto> authorityDTOs = category.getAuthorities().stream()
            .map(this::toAuthorityDto)
            .toList();

        return AuthorityCategoryDto.builder()
            .id(category.getId())
            .nameEn(category.getNameEn())
            .nameUk(category.getNameUk())
            .authorities(authorityDTOs)
            .build();
    }
}
