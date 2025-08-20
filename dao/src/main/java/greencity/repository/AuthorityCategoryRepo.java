package greencity.repository;

import greencity.entity.AuthorityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityCategoryRepo extends JpaRepository<AuthorityCategory, Long> {
}
