package greencity.service;

import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.enums.RetryableTaskType;

public interface RetryableTaskProcessor {
    RetryableTaskType getRetryableTaskType();

    void process(RetryableTaskVO retryableTaskVO);
}
