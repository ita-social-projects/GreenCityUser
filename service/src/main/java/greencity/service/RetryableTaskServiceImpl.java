package greencity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import greencity.repository.RetryableTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryableTaskServiceImpl implements RetryableTaskService {
    private final RetryableTaskRepository retryableTaskRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public <T> void saveRetryableTask(T payloadObject) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payloadObject);

            RetryableTask task = RetryableTask.builder()
                .payload(jsonPayload)
                .type(RetryableTaskType.SEND_GREENCITY_REQUEST)
                .status(RetryableTaskStatus.IN_PROGRESS)
                .build();

            retryableTaskRepository.save(task);
            log.info("Saved retryable task of type: {}", RetryableTaskType.SEND_GREENCITY_REQUEST);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}
