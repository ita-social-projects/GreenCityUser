package greencity.repository;

import greencity.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AuthorityRepo extends JpaRepository<Authority, Long> {
    /**
     * Gets all Employee authorities by employee id.
     *
     * @return Set of {@link String}.
     */
    @Query(value = """
        SELECT DISTINCT name from employee_authorities\
         inner join employee_authorities_mapping eam on employee_authorities.id = eam.authority_id \
        where user_id = :userId\
        """, nativeQuery = true)
    Set<String> getAuthoritiesByEmployeeId(@Param(value = "userId") Long employeeId);

    /**
     * Method that return list of authorities.
     *
     * @param name - list of positions name.
     * @return list of authorities.
     */
    @Query(
        value = """
            SELECT DISTINCT au FROM Authority au LEFT JOIN au.positions pos \
            WHERE pos.name IN (:name) or pos.nameEn IN (:name)\
            """)
    List<Authority> findAuthoritiesByPositions(List<String> name);

    /**
     * Method that return list of authorities by names.
     * 
     * @param name - list of authorities name.
     * @return - list of authorities.
     */
    @Query(value = "SELECT au FROM Authority au WHERE au.name in (:name)")
    List<Authority> findAuthoritiesByNames(List<String> name);
}
