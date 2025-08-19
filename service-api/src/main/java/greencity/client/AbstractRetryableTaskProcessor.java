package greencity.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.enums.RetryableTaskType;
import greencity.exception.exceptions.TaskProcessingException;
import greencity.service.RetryableTaskProcessor;

public abstract class AbstractRetryableTaskProcessor<T> implements RetryableTaskProcessor {
    private final ObjectMapper objectMapper;
    private final RetryableTaskType retryableTaskType;

    @Override
    public RetryableTaskType getRetryableTaskType() {
        return this.retryableTaskType;
    }

    protected AbstractRetryableTaskProcessor(ObjectMapper objectMapper, RetryableTaskType retryableTaskType) {
        this.objectMapper = objectMapper;
        this.retryableTaskType = retryableTaskType;
    }

    @Override
    public void process(RetryableTaskVO retryableTaskVO) {
        try {
            T payload = objectMapper.readValue(retryableTaskVO.getPayload(), getPayloadClass());
            handle(payload);
        } catch (Exception e) {
            throw new TaskProcessingException("Failed to process retryable task", e);
        }
    }

    protected abstract Class<T> getPayloadClass();

    protected abstract void handle(T payload);
}
