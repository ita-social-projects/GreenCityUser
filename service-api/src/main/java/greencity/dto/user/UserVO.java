package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserVO extends UserVOReducedDto {
}
