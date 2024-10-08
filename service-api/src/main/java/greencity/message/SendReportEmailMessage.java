package greencity.message;

import greencity.dto.category.CategoryDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.SubscriberDto;
import greencity.enums.EmailNotification;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendReportEmailMessage {
    private List<SubscriberDto> subscribers;
    private Map<CategoryDto, List<PlaceNotificationDto>> categoriesDtoWithPlacesDtoMap;
    private EmailNotification emailNotification;
}
