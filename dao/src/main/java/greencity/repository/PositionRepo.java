package greencity.repository;

import greencity.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides an interface to manage {@link Position} entity.
 */
@Repository
public interface PositionRepo extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {
    /**
     * Method, that return {@link Position} by list of position names.
     *
     * @param positionNames - list of position names
     * @return list of {@link Position}
     * @author Anton Bondar
     */
    @Query("SELECT p FROM Position p WHERE p.name IN(:positionNames)")
    List<Position> findPositionsByNames(List<String> positionNames);
}
