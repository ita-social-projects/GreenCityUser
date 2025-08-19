package greencity.client;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import greencity.enums.RetryableTaskType;
import greencity.service.RetryableTaskProcessor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetryableTaskProcessorRegistryTest {

    @Test
    void getProcessor() {
        RetryableTaskProcessor mockProcessor = mock(RetryableTaskProcessor.class);
        when(mockProcessor.getRetryableTaskType()).thenReturn(RetryableTaskType.CREATE_USER);

        List<RetryableTaskProcessor> processors = List.of(mockProcessor);
        RetryableTaskProcessorRegistry registry = new RetryableTaskProcessorRegistry(processors);

        RetryableTaskProcessor result = registry.getProcessor(RetryableTaskType.CREATE_USER);

        assertSame(mockProcessor, result);
    }

    @Test
    void getProcessorReturnsNullForUnknownType() {
        RetryableTaskProcessorRegistry registry = new RetryableTaskProcessorRegistry(List.of());

        RetryableTaskProcessor result = registry.getProcessor(RetryableTaskType.CREATE_USER);

        assertNull(result);
    }
}