package greencity.filters;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchCriteria {
    private Object value;
    private String key;
    private String type;
}
