package greencity.service;

import greencity.enums.RetryableTaskType;

public interface RetryableTaskService {
    <T> void saveRetryableTask(T payloadObject, RetryableTaskType type);
}
