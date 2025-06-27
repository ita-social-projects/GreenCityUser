package greencity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import greencity.exception.exceptions.RetryableTaskSerializationException;
import greencity.repository.RetryableTaskRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryableTaskServiceImpl implements RetryableTaskService {
    private final RetryableTaskRepository retryableTaskRepository;
    private final ObjectMapper objectMapper;
    private static final Integer LIMIT = 100;
    private static final Integer TIMEOUTINSECONDS = 60;

    @Transactional
    public <T> void saveRetryableTask(T payloadObject, RetryableTaskType type) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payloadObject);
            RetryableTask task = RetryableTask.builder()
                .payload(jsonPayload)
                .type(type)
                .status(RetryableTaskStatus.IN_PROGRESS)
                .retryTime(LocalDateTime.now())
                .build();
            retryableTaskRepository.save(task);
            log.info("Saved retryable task of type: {}", type);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            throw new RetryableTaskSerializationException("Failed to serialize payload", e);
        }
    }

    @Transactional
    public List<RetryableTask> getRetryableTaskForProcessing() {
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, LIMIT);
        List<RetryableTask> retryableTasks = retryableTaskRepository.findRetryableTaskForProcessing(
            LocalDateTime.now(), RetryableTaskStatus.IN_PROGRESS, pageable);
        for (RetryableTask task : retryableTasks) {
            task.setRetryTime(currentTime.plus(Duration.ofSeconds(TIMEOUTINSECONDS)));
        }
        return retryableTasks;
    }
}
