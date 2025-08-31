package greencity.mapping;

import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.entity.RetryableTask;
import org.modelmapper.AbstractConverter;

public class RetryableTaskMapper extends AbstractConverter<RetryableTask, RetryableTaskVO> {
    @Override
    protected RetryableTaskVO convert(RetryableTask task) {
        return RetryableTaskVO.builder()
            .payload(task.getPayload())
            .type(task.getType())
            .status(task.getStatus())
            .createdAt(task.getCreatedTime())
            .retryAt(task.getRetryTime())
            .build();
    }
}
