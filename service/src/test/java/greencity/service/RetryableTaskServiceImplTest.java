package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import greencity.exception.exceptions.RetryableTaskSerializationException;
import greencity.repository.RetryableTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RetryableTaskServiceImplTest {
    @Mock
    private RetryableTaskRepository retryableTaskRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RetryableTaskServiceImpl retryableTaskService;

    @Test
    void saveRetryableTaskTest() throws JsonProcessingException {
        RetryableTaskType type = RetryableTaskType.CREATE_USER;
        CreateGreenCityUserDto dto = ModelUtils.getCreateGreenCityDto();

        String expectedPayload = new ObjectMapper().writeValueAsString(dto);

        when(objectMapper.writeValueAsString(dto)).thenReturn(expectedPayload);

        ArgumentCaptor<RetryableTask> taskCaptor = ArgumentCaptor.forClass(RetryableTask.class);

        retryableTaskService.saveRetryableTask(dto, type);

        verify(objectMapper).writeValueAsString(dto);
        verify(retryableTaskRepository).save(taskCaptor.capture());

        RetryableTask savedTask = taskCaptor.getValue();
        assertEquals(expectedPayload, savedTask.getPayload());
        assertEquals(type, savedTask.getType());
        assertEquals(RetryableTaskStatus.IN_PROGRESS, savedTask.getStatus());
        assertNotNull(savedTask.getRetryTime());
    }

    @Test
    void saveRetryableTask_shouldThrowCustomException() throws JsonProcessingException {
        RetryableTaskType type = RetryableTaskType.CREATE_USER;
        CreateGreenCityUserDto dto = new CreateGreenCityUserDto();

        when(objectMapper.writeValueAsString(dto)).thenThrow(new JsonProcessingException("Serialization error"){});

        RetryableTaskSerializationException exception = assertThrows(
            RetryableTaskSerializationException.class,
            () -> retryableTaskService.saveRetryableTask(dto, type)
        );

        assertTrue(exception.getMessage().contains("Failed to serialize payload"));
        verify(objectMapper).writeValueAsString(dto);
        verifyNoInteractions(retryableTaskRepository);
    }

    @Test
    void getRetryableTaskTest() throws JsonProcessingException {
        RetryableTaskType type = RetryableTaskType.CREATE_USER;
        List<RetryableTask> tasks = ModelUtils.getRetryableTasks();

        when(retryableTaskRepository.findRetryableTaskForProcessing(
            eq(type), any(LocalDateTime.class), eq(RetryableTaskStatus.IN_PROGRESS), any(Pageable.class)))
            .thenReturn(tasks);

        retryableTaskService.getRetryableTaskForProcessing(type);

        verify(retryableTaskRepository).findRetryableTaskForProcessing(
            eq(type), any(LocalDateTime.class), eq(RetryableTaskStatus.IN_PROGRESS), any(Pageable.class));
    }
}
