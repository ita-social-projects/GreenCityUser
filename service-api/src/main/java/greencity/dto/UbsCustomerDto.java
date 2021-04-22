package greencity.dto;

import lombok.*;

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
