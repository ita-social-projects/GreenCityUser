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
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private final UserRepo userRepo;
    private final AuthorityCategoryRepo authorityCategoryRepo;
    private final ModelMapper modelMapper;
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

        List<Long> positionIds = dto.getPositions().stream().map(PositionDto::getId).toList();
        List<Position> positions = positionRepo.findAllById(positionIds);
        List<Authority> authorities = authorityRepo.findAllByPositionIdsIn(positionIds);

        employee.setPositions(positions);
        employee.setAuthorities(authorities);
        userRepo.save(employee);
    }

    @Override
    public List<AuthorityDto> getAuthoritiesByCategory(Long categoryId) {
        List<Authority> authorities = authorityRepo.findAllByCategoryId(categoryId);
        return authorities.stream()
            .map(authority -> modelMapper.map(authority, AuthorityDto.class))
            .toList();
    }

    @Override
    public List<AuthorityCategoryDto> getAllAuthorityCategories() {
        List<AuthorityCategory> categories = authorityCategoryRepo.findAll();
        return categories.stream()
            .map(category -> modelMapper.map(category, AuthorityCategoryDto.class))
            .toList();
    }

    @Override
    public List<AuthorityCategoryDto> getEmployeesAuthoritiesGroupedByCategories(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));

        List<Authority> authorities = user.getAuthorities();

        return authorities.stream()
            .collect(Collectors.groupingBy(Authority::getCategory))
            .entrySet().stream()
            .map(entry -> {
                AuthorityCategoryDto categoryDto = modelMapper.map(entry.getKey(), AuthorityCategoryDto.class);
                List<AuthorityDto> authorityDTOs = entry.getValue().stream()
                    .map(auth -> modelMapper.map(auth, AuthorityDto.class))
                    .toList();
                categoryDto.setAuthorities(authorityDTOs);
                return categoryDto;
            })
            .toList();
    }
}
