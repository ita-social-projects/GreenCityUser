package greencity.config;

import greencity.client.RetryableTaskProcessorRegistry;
import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.entity.RetryableTask;
import greencity.enums.RetryableTaskStatus;
import greencity.exception.exceptions.TaskProcessingException;
import greencity.repository.RetryableTaskRepository;
import greencity.service.RetryableTaskProcessor;
import greencity.service.RetryableTaskServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryableTaskScheduler {
    private final RetryableTaskRepository repository;
    private final RetryableTaskServiceImpl retryableTaskService;
    private final RetryableTaskProcessorRegistry registry;
    private final ModelMapper modelMapper;

    @Scheduled(fixedRate = 21600000)
    public void executePendingTasks() {
        List<RetryableTask> tasks = retryableTaskService.getRetryableTaskForProcessing();
        for (RetryableTask task : tasks) {
            try {
                RetryableTaskVO taskVO = modelMapper.map(task, RetryableTaskVO.class);
                RetryableTaskProcessor processor = registry.getProcessor(task.getType());
                log.info("Retrieved processor for type {}: {}", task.getType(), processor);
                processor.process(taskVO);

                task.setStatus(RetryableTaskStatus.SUCCESS);
                repository.save(task);
            } catch (TaskProcessingException e) {
                log.warn("TaskProcessingException for task {}: {}", task.getId(), e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error for task {}: {}", task.getId(), e.getMessage(), e);
            }
        }
    }
}
