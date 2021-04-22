package greencity.dto;

import lombok.*;
import org.springframework.context.annotation.Role;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class UbsCustomerDto {
    private String name;
    private String email;
    private String phoneNumber;
}
