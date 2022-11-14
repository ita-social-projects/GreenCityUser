package greencity;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.violation.UserViolationMailDto;
import greencity.entity.User;
import greencity.enums.Role;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email("mail@gmail.com")
            .name(TestConst.NAME)
            .role(Role.ROLE_UBS_EMPLOYEE)
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

    public static UserViolationMailDto getUserViolationMailDto() {
        return UserViolationMailDto.builder()
            .email("string@gmail.com")
            .name("string")
            .violationDescription("String Description")
            .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder().email("test@gmail.com").role(Role.ROLE_ADMIN).build();
    }
}
