package greencity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RetryableTaskStatus {
    IN_PROGRESS,
    SUCCESS
}
