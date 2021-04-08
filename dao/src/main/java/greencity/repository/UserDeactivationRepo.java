package greencity.repository;

import greencity.entity.UserDeactivationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeactivationRepo extends JpaRepository<UserDeactivationReason, Long> {
    /**
     * {@inheritDoc}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM reasons_for_user_deactivation where id_user = :id and"
        + " date_of_deactivation=(select MAX(date_of_deactivation) from reasons_for_user_deactivation)")
    Optional<UserDeactivationReason> getLastDeactivationReasons(Long id);
}
