package greencity.filters;

import lombok.*;

import java.util.Arrays;
import java.util.Objects;

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
