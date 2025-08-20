package greencity.security.service;

import greencity.dto.EmployeePositionsDto;
import greencity.dto.authorities.AuthorityCategoryDto;
import greencity.dto.position.PositionDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import java.util.List;
import java.util.Set;

public interface AuthorityService {
    /**
     * Retrieves all authorities assigned to the employee by their email.
     *
     * @param email the employee's email
     * @return a {@link Set} of authority names (technical identifiers).
     */
    Set<String> getAllEmployeesAuthorities(String email);

    /**
     * Updates the set of authorities directly assigned to an employee.
     * <p>
     * Existing authorities will be replaced with those provided in the DTO.
     * </p>
     *
     * @param dto the {@link UserEmployeeAuthorityDto} containing
     *            employee email and list of authority names.
     */
    void updateEmployeesAuthorities(UserEmployeeAuthorityDto dto);

    /**
     * Updates an employee's authorities based on their positions.
     * <p>
     * The employee's positions will be updated, and their authorities
     * will be recalculated according to the linked positions.
     * </p>
     *
     * @param dto contains employee email and a list of {@link PositionDto}.
     */
    void updateAuthoritiesToRelatedPositions(EmployeePositionsDto dto);

    /**
     * Retrieves all authorities assigned to a specific employee,
     * grouped by categories.
     *
     * @param email the employee's email
     * @return a list of {@link AuthorityCategoryDto} containing categories
     *         and only the authorities that this employee has.
     */
    List<AuthorityCategoryDto> getEmployeesAuthoritiesGroupedByCategories(String email);
}
