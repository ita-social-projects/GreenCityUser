package greencity.security.repository;

import greencity.entity.VerifyEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link VerifyEmail}.
 */
@Repository
public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    /**
     * Finds a record by userId and email verification token.
     *
     * @param userId - {@link Long} user's id
     * @param token  - {@link String} email verification token
     * @return - not empty {@link Optional} if a record with given userId and token
     *         exists.
     */
    Optional<VerifyEmail> findByTokenAndUserId(String token, Long userId);

    /**
     * Deletes record for a given userId and email verification token.
     *
     * @param userId - {@link Long} user's id
     * @param token  - {@link String} email verification token
     */
    @Modifying
    @Query("DELETE FROM VerifyEmail WHERE token = :token AND user.id = :userId")
    void deleteByTokenAndUserId(String token, Long userId);
}
