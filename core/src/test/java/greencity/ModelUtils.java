package greencity;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import java.util.List;

public class ModelUtils {
    public static final UserVO TEST_USER_VO = createUserVO();

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .build();
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();

    }

    public static UserManagementDto getUserManagementDto() {
        return UserManagementDto.builder()
            .id(1L)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .email(TestConst.EMAIL)
            .userCredo(TestConst.CREDO)
            .build();
    }

    public static UserManagementUpdateDto getUserManagementUpdateDto() {
        return UserManagementUpdateDto.builder()
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .email(TestConst.EMAIL)
            .userCredo(TestConst.CREDO)
            .build();
    }

    public static PageableAdvancedDto<UserManagementDto> getPageableAdvancedDto() {
        return new PageableAdvancedDto<>(List.of(getUserManagementDto()), 1L, 1, 1, 1, false, false, true, true);
    }

    private static UserVO createUserVO() {
        return UserVO.builder().email("test@gmail.com").role(Role.ROLE_ADMIN).build();
    }
}
