package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetryableTaskMapperTest {
    @InjectMocks
    private RetryableTaskMapper mapper;
    private LocalDateTime createdAt;
    private LocalDateTime retryAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now();
        retryAt = LocalDateTime.now().plusMinutes(5);
    }

    @Test
    void convert() {
        RetryableTask retryableTask = RetryableTask.builder()
            .id(1L)
            .payload("payload")
            .type(RetryableTaskType.CREATE_USER)
            .status(RetryableTaskStatus.IN_PROGRESS)
            .createdTime(createdAt)
            .retryTime(retryAt)
            .build();

        RetryableTaskVO expected = RetryableTaskVO.builder()
            .payload("payload")
            .type(RetryableTaskType.CREATE_USER)
            .status(RetryableTaskStatus.IN_PROGRESS)
            .createdAt(createdAt)
            .retryAt(retryAt)
            .build();

        assertEquals(expected, mapper.convert(retryableTask));
    }
}