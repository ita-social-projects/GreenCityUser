package greencity.dto.ubs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UbsProfileCreationDto {
    @NotBlank
    private String uuid;
    @Email
    private String email;
    @Size(min = 1, max = 30, message = "name must have no less than 1 and no more than 30 symbols")
    @Pattern(regexp = "^(?!\\.)(?!.*\\.$)(?!.*?\\.\\.)(?!.*?\\-\\-)(?!.*?\\'\\')[-'ʼ ґҐіІєЄїЇА-Яа-я+\\w.]{1,30}$",
        message = "name must contain only \"ЁёІіЇїҐґЄєА-Яа-яA-Za-z-'0-9 .\", dot can only be in the center of the name")
    private String name;
}
