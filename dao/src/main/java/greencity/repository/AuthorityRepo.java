package greencity.repository;

import greencity.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface AuthorityRepo extends JpaRepository<Authority, Long> {
    /**
     * Gets all Employee authorities by employee id.
     *
     * @return Set of {@link String}.
     */
    @Query(value = "SELECT DISTINCT name from employee_authorities"
            + " inner join employee_authorities_mapping eam on employee_authorities.id = eam.authority_id "
            + "where user_id = :userId", nativeQuery = true)
    Set<String> getAuthoritiesByEmployeeId(@Param(value = "userId") Long employeeId);
}
