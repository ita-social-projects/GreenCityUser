package greencity.repository;

import greencity.entity.AuthorityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorityCategoryRepo extends JpaRepository<AuthorityCategory, Long> {
    /**
     * Retrieves all authority categories.
     *
     * @return list of {@link AuthorityCategory}.
     */
    @Query("SELECT ac FROM AuthorityCategory ac")
    List<AuthorityCategory> findAllCategories();
}
