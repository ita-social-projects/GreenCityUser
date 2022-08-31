package greencity.security.service;

import greencity.dto.user.UserEmployeeAuthorityDto;
import greencity.entity.*;

import java.util.List;
import java.util.Set;

public interface AuthorityService {
    /**
     * Method gets all Employee authorities.
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
}
