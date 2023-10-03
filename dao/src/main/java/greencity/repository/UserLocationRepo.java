package greencity.repository;

import greencity.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLocationRepo extends JpaRepository<UserLocation, Long> {
}
