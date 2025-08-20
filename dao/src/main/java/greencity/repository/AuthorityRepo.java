package greencity.repository;

import greencity.entity.Authority;
import greencity.entity.AuthorityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AuthorityRepo extends JpaRepository<Authority, Long> {
    /**
     * Gets all Employee authorities by employee id.
     *
     * @return Set of {@link String}.
     */
    @Query(
        value = "SELECT DISTINCT name from employee_authorities "
            + "INNER JOIN employee_authorities_mapping eam ON employee_authorities.id = eam.authority_id "
            + "where user_id = :userId",
        nativeQuery = true)
    Set<String> getAuthoritiesByEmployeeId(@Param(value = "userId") Long employeeId);

    /**
     * Method that return list of authorities.
     *
     * @param name - list of positions name.
     * @return list of authorities.
     */
    @Query(
        value = "SELECT DISTINCT au FROM Authority au LEFT JOIN au.positions pos "
            + "WHERE pos.nameUk IN (:name) or pos.nameEn IN (:name) ")
    List<Authority> findAuthoritiesByPositions(List<String> name);

    /**
     * Finds all authorities by related position ids.
     *
     * @param positionIds list of position IDs.
     * @return list of {@link Authority}.
     */
    @Query(
        value = "SELECT DISTINCT au FROM Authority au LEFT JOIN au.positions pos "
            + "WHERE pos.id IN (:positionIds)")
    List<Authority> findAllByPositionIdsIn(Collection<Long> positionIds);

    /**
     * Method that return list of authorities by names.
     * 
     * @param name - list of authorities name.
     * @return - list of authorities.
     */
    @Query(value = "SELECT au FROM Authority au WHERE au.name in (:name)")
    List<Authority> findAuthoritiesByNames(List<String> name);

    /**
     * Retrieves all authorities with their categories eagerly fetched.
     *
     * @return list of {@link Authority} with categories initialized.
     */
    @Query("SELECT au FROM Authority au JOIN FETCH au.category")
    List<Authority> findAllWithCategories();

    /**
     * Retrieves all authorities that belong to the given category.
     *
     * @param categoryId ID of the {@link AuthorityCategory}.
     * @return list of {@link Authority}.
     */
    @Query("SELECT a FROM Authority a WHERE a.category.id = :categoryId")
    List<Authority> findAllByCategoryId(@Param("categoryId") Long categoryId);
}
