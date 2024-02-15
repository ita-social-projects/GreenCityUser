package greencity.security.repository;

import greencity.entity.OwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link OwnSecurity}.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Repository
public interface OwnSecurityRepo extends JpaRepository<OwnSecurity, Long> {
    /**
     * Updates a password.
     *
     * @param password {@link String}
     * @param id       {@link Long}
     * @author Dmytro Dovhal
     */
    @Modifying
    @Query("UPDATE OwnSecurity o SET o.password = :password WHERE o.user.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    /**
     * Finds user by id.
     *
     * @param id {@link Long}
     * @author Ihor Volianskyi
     */
    Optional<OwnSecurity> findByUserId(Long id);
}
