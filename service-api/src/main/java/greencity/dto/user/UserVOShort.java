package greencity.dto.user;

import greencity.dto.language.LanguageVO;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
public class UserVOShort {
    private Long id;

    private String name;

    private String email;

    private Role role;

    private UserStatus userStatus;

    private LanguageVO languageVO;
}
