package greencity.repository;

import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RetryableTaskRepository extends JpaRepository<RetryableTask, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r from RetryableTask r where r.type= :type " +
        "AND r.retryTime<= :retryTime " +
        "AND r.status= :status " +
        "order by r.retryTime asc")
    List<RetryableTask> findRetryableTaskForProcessing(RetryableTaskType type, LocalDateTime retryTime,
        RetryableTaskStatus status, Pageable pageable);

}
