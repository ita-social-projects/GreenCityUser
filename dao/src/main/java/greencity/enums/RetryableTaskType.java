package greencity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RetryableTaskType {
    UPDATE_USER_RATING,
    UPDATE_USERNAME,
    UPDATE_USER_CREDO,
    CREATE_USER,
    SET_LOCATION_FOR_USER,
    UPDATE_USER_PICTURE_PATH
}
