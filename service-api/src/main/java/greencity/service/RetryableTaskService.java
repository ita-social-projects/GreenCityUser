package greencity.service;

public interface RetryableTaskService {
    <T> void saveRetryableTask(T payloadObject);
}
