package greencity.security.service;

import greencity.dto.UpdateEmployeeAuthoritiesDto;
import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.User;
import java.util.Set;

public interface AuthorityService {
    /**
     * Method gets all employee's authorities.
     *
     * @return Set of {@link String}.
     */
    Set<String> getAllEmployeesAuthorities(String email);

    /**
     * Method updates Authority for {@link User}.
     *
     * @param dto - instance of {@link UserEmployeeAuthorityDto}.
     */
    void updateEmployeesAuthorities(UserEmployeeAuthorityDto dto, String email);

    /**
     * Method that update authorities for employee.
     *
     * @param dto contains email and list of positions.
     * @author Nikita Korzh.
     */
    void updateAuthorities(UpdateEmployeeAuthoritiesDto dto);
}
