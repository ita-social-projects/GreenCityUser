package greencity.dto.retryabletask;

import greencity.enums.RetryableTaskStatus;
import greencity.enums.RetryableTaskType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RetryableTaskVO {
    private String payload;

    private RetryableTaskType type;

    private RetryableTaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime retryAt;
}
