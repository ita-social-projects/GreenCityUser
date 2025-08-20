package greencity.security.service;

import static greencity.ModelUtils.TEST_EMAIL;
import static greencity.ModelUtils.createEmployee;
import static greencity.ModelUtils.getAuthority;
import static greencity.ModelUtils.getAuthorityCategory;
import static greencity.ModelUtils.getPositions;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserEmployeeAuthorityDto;
import greencity.constant.ErrorMessage;
import greencity.dto.EmployeePositionsDto;
import greencity.dto.authorities.AuthorityCategoryDto;
import greencity.dto.authorities.AuthorityDto;
import greencity.dto.position.PositionDto;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorityServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthorityRepo authorityRepo;
    @Mock
    private PositionRepo positionRepo;
    @Mock
    private Authentication auth;
    @Mock
    private AuthorityCategoryRepo authorityCategoryRepo;
    @InjectMocks
    private AuthorityServiceImpl authorityService;

    @BeforeEach
    void initSecurityContext() {
        when(auth.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllEmployeesAuthoritiesTest() {
        Set<String> expected = new HashSet<>();
        expected.add("test");
        expected.add("test1");
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(getUser()));
        when(authorityRepo.getAuthoritiesByEmployeeId(getUser().getId())).thenReturn(expected);
        assertEquals(expected, authorityService.getAllEmployeesAuthorities(TEST_EMAIL));

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(authorityRepo).getAuthoritiesByEmployeeId(getUser().getId());
    }

    @Test
    void updateEmployeesAuthoritiesTest() {
        User employee = createEmployee();
        List<Authority> authority = List.of(getAuthority());
        List<String> authoritiesName = authority.stream().map(Authority::getName).toList();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        when(authorityRepo.findAuthoritiesByNames(authoritiesName)).thenReturn(authority);

        employee.setAuthorities(authority);
        authorityService.updateEmployeesAuthorities(getUserEmployeeAuthorityDto());

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(authorityRepo).findAuthoritiesByNames(authoritiesName);
        verify(userRepo).save(employee);
    }

    @Test
    void updateEmployeesAuthoritiesThrowsNotFoundExceptionTest() {
        var dto = getUserEmployeeAuthorityDto();
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> authorityService.updateEmployeesAuthorities(dto));
        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void updateEmployeesAuthoritiesThrowsBadRequestExceptionTest() {
        User employee = createEmployee();
        employee.setRole(Role.ROLE_USER);

        var dto = getUserEmployeeAuthorityDto();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));

        assertEquals(Role.ROLE_USER, employee.getRole());
        assertThrows(BadRequestException.class,
            () -> authorityService.updateEmployeesAuthorities(dto));

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void updateAuthoritiesToRelatedPositionsTest() {
        User employee = createEmployee();
        List<Position> positions = getPositions();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        when(positionRepo.findAllById(List.of(1L))).thenReturn(positions);
        when(authorityRepo.findAllByPositionIdsIn(List.of(1L))).thenReturn(List.of(getAuthority()));

        authorityService.updateAuthoritiesToRelatedPositions(EmployeePositionsDto.builder()
            .email(TEST_EMAIL)
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .nameUk("Супер адмін")
                .nameEn("Super admin")
                .build()))
            .build());

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(positionRepo).findAllById(List.of(1L));
        verify(authorityRepo).findAllByPositionIdsIn(List.of(1L));
    }

    @Test
    void updateAuthoritiesToRelatedPositionsThrowsNotFoundExceptionTest() {
        var dto = new EmployeePositionsDto();
        assertThrows(UsernameNotFoundException.class, () -> authorityService.updateAuthoritiesToRelatedPositions(dto));
    }

    @Test
    void getEmployeesAuthoritiesGroupedByCategoriesTest() {
        User employee = createEmployee();
        Authority authority = getAuthority();

        employee.setAuthorities(List.of(authority));

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(employee));
        List<AuthorityCategoryDto> result = authorityService.getEmployeesAuthoritiesGroupedByCategories(TEST_EMAIL);

        assertEquals(1, result.size());
        AuthorityCategoryDto categoryDto = result.getFirst();
        assertEquals(authority.getCategory().getNameEn(), categoryDto.getNameEn());
        assertEquals(authority.getCategory().getNameUk(), categoryDto.getNameUk());

        assertEquals(1, categoryDto.getAuthorities().size());
        AuthorityDto authorityDto = categoryDto.getAuthorities().getFirst();
        assertEquals(authority.getName(), authorityDto.getName());
        assertEquals(authority.getDescriptionEn(), authorityDto.getDescriptionEn());
        assertEquals(authority.getDescriptionUk(), authorityDto.getDescriptionUk());

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void getAuthoritiesByCategoryTest() {
        Long categoryId = 1L;
        Authority authority = getAuthority();
        AuthorityCategory category = getAuthorityCategory();

        when(authorityCategoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(authorityRepo.findAllByCategoryId(categoryId)).thenReturn(List.of(authority));

        List<AuthorityDto> result = authorityService.getAuthoritiesByCategory(categoryId);

        assertEquals(1, result.size());
        AuthorityDto authorityDto = result.getFirst();
        assertEquals(authority.getName(), authorityDto.getName());
        assertEquals(authority.getDescriptionEn(), authorityDto.getDescriptionEn());
        assertEquals(authority.getDescriptionUk(), authorityDto.getDescriptionUk());

        verify(authorityRepo).findAllByCategoryId(categoryId);
    }

    @Test
    void getAuthoritiesByCategoryShouldThrowIfCategoryNotFound() {
        Long categoryId = 999L;

        when(authorityCategoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        NotFoundException exception =
            assertThrows(NotFoundException.class, () -> authorityService.getAuthoritiesByCategory(categoryId));

        assertEquals(String.format(ErrorMessage.AUTHORITY_CATEGORY_NOT_FOUND, categoryId), exception.getMessage());

        verify(authorityCategoryRepo).findById(categoryId);
        verifyNoInteractions(authorityRepo);
    }

    @Test
    void getAllAuthorityCategoriesTest() {
        AuthorityCategory category = AuthorityCategory.builder()
            .id(1L)
            .nameEn("Clients")
            .nameUk("Клієнти")
            .authorities(Collections.emptyList())
            .build();

        when(authorityCategoryRepo.findAll()).thenReturn(List.of(category));

        List<AuthorityCategoryDto> result = authorityService.getAllAuthorityCategories();

        assertEquals(1, result.size());
        AuthorityCategoryDto categoryDto = result.getFirst();
        assertEquals("Clients", categoryDto.getNameEn());
        assertEquals("Клієнти", categoryDto.getNameUk());

        verify(authorityCategoryRepo).findAll();
    }
}
