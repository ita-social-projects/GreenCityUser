package greencity.client;

import greencity.enums.RetryableTaskType;
import greencity.service.RetryableTaskProcessor;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryableTaskProcessorRegistry {
    private Map<RetryableTaskType, RetryableTaskProcessor> registry = new EnumMap<>(RetryableTaskType.class);

    public RetryableTaskProcessorRegistry(List<RetryableTaskProcessor> processors) {
        for (RetryableTaskProcessor processor : processors) {
            registry.put(processor.getRetryableTaskType(), processor);
        }
    }

    public RetryableTaskProcessor getProcessor(RetryableTaskType retryableTaskType) {
        return registry.get(retryableTaskType);
    }
}
