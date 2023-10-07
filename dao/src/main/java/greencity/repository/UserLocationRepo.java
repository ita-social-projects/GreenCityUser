package greencity.repository;

import greencity.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLocationRepo extends JpaRepository<UserLocation, Long> {
    /**
     * Gets the UserLocation by latitude and longitude.
     */
    Optional<UserLocation> getUserLocationByLatitudeAndLongitude(Double latitude, Double longitude);
}
