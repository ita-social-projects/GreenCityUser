package greencity.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AsyncConfigTest {
    @Test
    void testCustomTaskExecutor() {
        AsyncConfig asyncConfig = new AsyncConfig();
        ThreadPoolTaskExecutor executor = asyncConfig.customTaskExecutor();
        assertEquals(5, executor.getCorePoolSize());
        assertEquals(10, executor.getMaxPoolSize());
        assertEquals(25, executor.getQueueCapacity());
        assertEquals("Parallel-Thread-", executor.getThreadNamePrefix());
    }
}
